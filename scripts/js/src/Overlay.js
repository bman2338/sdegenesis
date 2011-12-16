var skipElements = ["Revision","Author"];

var elementsSelected = [];
var hidden = true;

var canvas_width = 1200;
var canvas_height = 300;

function showHide() {
	if(hidden) {
		$( "#overlay-transparent" ).show( 'blind', {}, 600);
		$('#node-selection').click(updateResults);
		$( "#show-hide" ).html('<a class="topnav" onclick="showHide()" href="#" target="_top">Hide Tools</a>');
		canvas_width = $( "#content-div" ).width();
		canvas_height = $( window ).height() - $( "#logo" ).height() - parseInt($( "#content-div" ).css("padding-top")) * 2 - 20;
		eventHandler.pause();
		hidden = false;
	} else {
		$( "#overlay-transparent" ).hide( 'blind', {}, 600);
		$( "#show-hide" ).html('<a class="topnav" onclick="showHide()" href="#" target="_top">Show Tools</a>');
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

function drawFunction (vis,analysis,nodes,types,override) {
	var canvas = "#chart";
	$(canvas).html("");
	var obj = {
		types: types,
		nodes: nodes,
	};
	if (override)
		showHide();
	vis.visualize(obj,canvas);
}

var globalAnalysisId;
var globalVisualizationId;
var globalElementsSelected;
var globalResultsToAnalyze;

function initialVisualization (analysisId,visualizationId,els,override) {	
	displayRevTag();
	
	var analysisAvailable = getAvailableAnalysis(els);
	var analysis = analysisAvailable[analysisId];
	
	globalVisualizationId = visualizationId;
	globalAnalysisId = analysisId;
	globalElementsSelected = owl.deepCopy(els);
	
	var useHistory = true;
	
	for (var i = 0; i < elementsSelected.length; ++i) {
		if (skipElements.indexOf(elementsSelected[i]) == -1) {
			useHistory = false;
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
	
			d3.select(window).on("keydown",	function() {   
				var displacement = 0;  
				switch (d3.event.keyCode) {
					case 37: previousRevision(); break;
					case 39: nextRevision(); break;
					default:
						return;
				}
			});
			if (override)
				globalResultsToAnalyze = owl.deepCopy(resultsToAnalyze);
			drawFunction(vis,analysis,globalResultsToAnalyze,elementsSelected,override);
		});
	} else {
		var history = projects[0][currentProject].history;
		var vis = bootstrapVisualization(analysis,visualizationId,history);		
		drawFunction(vis,analysis,[vis.source],elementsSelected,override);
	}
}

function displayRevTag() {
	if(projects[0][currentProject].currRev)
		$("#revtag").html("Current Workspace Revision: " + projects[0][currentProject].currRev).attr("float", "right");
	else
		$("#revtag").html("");	
}

	
function displayAnalysis (divId,elements) {
	displayRevTag();
		
		
	var div = $("#"+divId);
	div.html("");
	var tree = div.append("<ul class=\"tree\"></ul>").find('ul');

	$.each(elements, function(index, element) {
		var list = null;
		
		if (index == elements.length - 1) {
			list = tree.append("<li class=\"last top-analysis\">"+element.name+"</li>").find('li');
		} else {
			list = tree.append("<li class=\"top-analysis\">"+element.name+"</li>").find('li');
		}
		
		var innerTree;
		
		var visz = element.getVisualizations(element);
				
		var analysisIndex = index;
		
		$.each(visz,function(idx, elmn) {
			var innerTree = null;
			if(idx == 0 && index == 0) {
				innerTree = list.append("<ul></ul>").find('ul');
			}
			else {
				innerTree = list.append("<ul></ul>").find('ul').last();
			}
			
			var clickFun = "javascript:initialVisualization(" + analysisIndex + "," + idx +",elementsSelected,true)"
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
	
	var results = showResults();
	showAnalysis(elementsSelected,results);
	
}


function showResults() {
	var results = {};	
	
	projects[0][currentProject].getRevision(projects[0][currentProject], projects[0][currentProject].currRev, function(rev){
				
		var shouldSkip = [];

		$.each(elementsSelected,
			function(index, node) {
				if (skipElements.indexOf(node) != -1) {
					shouldSkip.push(index);
				} else {
					results[node] = rev.graph.getNodesByType(node);
				}
			});

		// alert(JSON.stringify(results));
		var resultDiv = document.getElementById("results");

		if (shouldSkip.length == elementsSelected.length) {
			resultDiv.innerHTML = "";
			return;
		}
			
			
		resultDiv.innerHTML = getTable(shouldSkip, results);
	
		$.each(elementsSelected,
			function(index, header) {
				sortList(header  + '-select'); 
			});
	});
}

function filterResults() {
	var results = {};	
	
	projects[0][currentProject].getRevision(projects[0][currentProject], projects[0][currentProject].currRev, function(rev){
				
		var shouldSkip = [];
				
		var query = document.getElementById("query").value;
	
		// alert(query)
	
		if(query != "") {
			results = getQueryResults(query, elementsSelected, rev.graph);
		} else {
		 	showResults();
			return;
		}

			
		var resultDiv = document.getElementById("results");
			
		if (shouldSkip.length == elementsSelected.length) {
			resultDiv.innerHTML = "";
			return;
		}
			
		resultDiv.innerHTML = getTable(shouldSkip, results);
	
		$.each(elementsSelected,
			function(index, header) {
				sortList(header  + '-select'); 
			});
	});
}


function getTable(shouldSkip, results) {
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
	return table;
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