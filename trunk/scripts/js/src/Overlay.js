var skipElements = ["Revision","Author"];
var elementsSelected = [];
var hidden = true;

var canvas_width = 1200;
var canvas_height = 300;

function showHide() {
	if(hidden) {
		$( "#overlay-transparent" ).show( 'blind', {}, 600);
		$('#node-selection').click(updateResults);
		$( "#show-hide" ).html('<a class="topnav" onclick="showHide()" href="#" target="_top">Hide</a>');
		canvas_width = $( "#content-div" ).width();
		canvas_height = $( window ).height() - $( "#logo" ).height() - parseInt($( "#content-div" ).css("padding-top")) * 2;
		eventHandler.pause();
		hidden = false;
	} else {
		$( "#overlay-transparent" ).hide( 'blind', {}, 600);
		$( "#show-hide" ).html('<a class="topnav" onclick="showHide()" href="#" target="_top">Show</a>');
		eventHandler.resume();
		hidden = true;
	}
};

function displayList(divId, elements, callback) {
	var div = document.getElementById(divId);
	var tree = div.append("<ul class=\"tree\"></ul>").find('ul');
	$.each(elements,function(index,element) {
		tree.append("<li>"+element+"</li>");
	});
}

function drawFunction (vis,analysis,nodes,types) {
	var canvas = "#chart";
	$(canvas).html("");
	var obj = {
		types: types,
		nodes: nodes,
	};
	showHide();
	vis.visualize(obj,canvas);
}

function initialVisualization (analysisId,visualizationId) {	
	var analysisAvailable = getAvailableAnalysis(elementsSelected);
	var analysis = analysisAvailable[analysisId];
	
	var useHistory = false;
	
	for (var i = 0; i < skipElements.length; ++i) {
		if (elementsSelected.indexOf(skipElements[i])) {
			useHistory = true;
			break;
		}
	}
	
	if (!useHistory) {
		projects[0][currentProject].getRevision(projects[0][currentProject], projects[0][currentProject].currRev, function(rev){
			var vis = bootstrapVisualization(analysis,visualizationId,rev.graph);
			var resultsToAnalyze = [];
			$.each(elementsSelected, function(index,header) {
				$("#" + header + "-select option:selected").each(function (index,element) {
					resultsToAnalyze.push(rev.graph.getNodeFromId(element.value));
				});
			});
			
			if (resultsToAnalyze.length == 0)
				return false;
	
			drawFunction(vis,analysis,resultsToAnalyze,elementsSelected);
		});
	} else {
		var history = projects[0][currentProject].history;
		var vis = bootstrapVisualization(analysis,visualizationId,history);		
		drawFunction(vis,analysis,[vis.source],elementsSelected);
	}
}
	
function displayAnalysis (divId,elements) {
	
	var div = $("#"+divId);
	div.html("");
	var tree = div.append("<ul class=\"tree\"></ul>").find('ul');

	alert(JSON.stringify(elements));

	$.each(elements, function(index, element) {
		var list = null;
		
		if (index == elements.length - 1) {
			list = tree.append("<li class=\"last top-analysis\">"+element.name+"</li>").find('li');
		} else {
			list = tree.append("<li class=\"top-analysis\">"+element.name+"</li>").find('li');
		}
		
		var innerTree;
		
		// var visz = element.getVisualizations(nodes);
		var visz = element.getVisualizations(element);
		
		alert(JSON.stringify(visz));
		
		var analysisIndex = index;
		
		$.each(visz,function(idx, elmn) {
			var innerTree = null;
			if(idx == 0 && index == 0) {
				innerTree = list.append("<ul></ul>").find('ul');
			}
			else {
				innerTree = list.append("<ul></ul>").find('ul').last();
			}
			
			var clickFun = "javascript:initialVisualization(" + analysisIndex + "," + idx +")"
			if (idx == visz.length - 1)
				innerTree.append("<li class=\"last\" onclick=\"" + clickFun + "\">"+elmn.name+"</li>");
			else
				innerTree.append("<li class=\"inner-viz\" onclick=\"" + clickFun + "\">"+elmn.name+"</li>");
		});				
	});
}

function displayMultiple(divId, elements) {
	var options = '<select id=\"node-selection\" multiple="multiple" size="4">';	
	
	$.each(elements,
		function(index, element) {
			options += '<option value=\"'+element.name+'\">' + element.value + '</option>'; 
		});
	
	options += '</select>';	
		
	document.getElementById(divId).innerHTML = options;
}

function updateResults() { 
	elementsSelected = [];

	$("#node-selection option:selected").each(function () {
      elementsSelected.push($(this).val());
	});
	
	var results = showResults(elementsSelected);
	showAnalysis(elementsSelected,results);
	
}

function showResults(elements) {
	var results = {};	
	
	projects[0][currentProject].getRevision(projects[0][currentProject], projects[0][currentProject].currRev, function(rev){
				
		var shouldSkip = [];
				
		$.each(elements,
			function(index, node) {
				if (skipElements.indexOf(node) != -1) {
					shouldSkip.push(index);
				}
				else {
					results[node] = rev.graph.getNodesByType(node);
				}
			});
			
			var resultDiv = document.getElementById("results");
			
		if (shouldSkip.length == elements.length) {
			resultDiv.innerHTML = "";
			return;
		}
			
		var table = '<table id=\"results-table\">';
		table += '<tr>'; 
		$.each(elementsSelected,
			function(index, header) {
				if (shouldSkip.indexOf(index) == -1) {
					table += '<td>' + header + '</td>';
				}
			});
		
		table += '</tr>'; 
		table += '<tr>';
		$.each(elementsSelected,
			function(index, header) {
				if (results[header]) {
					table += '<td><select class=\"candidates\" id="' + header  + '-select" multiple="multiple" size="20">';			
				$.each(results[header],
					function(indexx, element) {
						if (shouldSkip.indexOf(indexx) == -1) {
							table += '<option value=\"'+element.uniqueId+'\">' + element.properties.name + '</option>';
						}
					});
				table += '</td>';
				}
			});
		table += '</tr></table>';	
		resultDiv.innerHTML = table;
		$.each(elementsSelected,
			function(index, header) {
				sortList(header  + '-select'); 
			});
	});
}

// FIXME: FUCKING SLOW! Crash on Safari!
function sortList(id) {
    $("#" + id).each(function() {
    	var value = $(this).val();
        $(this).html($("option", $(this)).sort(function(aOption, anOption) {
            return aOption.text == anOption.text ? 0 : aOption.text < anOption.text ? -1 : 1
        }));
        $(this).val(value);
    });
}

function showAnalysis(analysis) {
	var analysisList = getAvailableAnalysis(analysis);
	displayAnalysis('analysis', analysisList);
}

function getAvailableAnalysis(nodes) {
	return analysisRegister.getEntries(nodes)
}