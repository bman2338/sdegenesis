function Toolbar(parentId, header, elements) {
	var containerDiv = document.getElementById(parentId);
	if(containerDiv) {
		// adding a child to the Toolbar
	} else {
		// creating a new Toolbar
		containerDiv = document.createElement('div');
		containerDiv.setAttribute('id', parentId);
	    h3 = document.createElement('h3');
	    a = document.createElement('a');
		toolDiv = document.createElement('div');
		ul = document.createElement('ul');
		$.each(elements,
			function(index, element) { 
				$(ul).append('<li>' + element + '</li>');
			});
		containerDiv.setAttribute('id', parentId);
		document.body.appendChild(containerDiv);
	}
	//     document.body.appendChild(tt);
	// alert(document.getElementById(parentId));

}

function showVizFor(analysis) {
	var visualizations = getVizFor(analysis);
	if($(visualizations).length > 0) {
		var newAccordion = '<h3><a href="#">Visualizations</a></h3><div><ul>';
		$.each(visualizations,
			function(index, viz) { 
				newAccordion += '<li><a href="#" onClick="javascript:showOptsFor(\'' + viz + '\');">' + viz.name() + '</a></li>';
			});	
		updateAccordion(newAccordion + '</ul></li>');
	} else {
		alert('No visualizations for this analysis');
	}
}

function showOptsFor(viz) {
	var options = getOptsFor(viz);
	if($(options).length > 0) {
		var newAccordion = '<h3><a href="#">Options</a></h3><div><ul>';
		$.each(options,
			function(index, option) { 
				newAccordion += '<li>' + option + '</li>';
			});	
		updateAccordion(newAccordion + '</ul></li>');
	} else {
		alert('No options to visualize!');
	}
}

function updateAccordion(additionalToolbar) {
	$('#toolbar').append(additionalToolbar).accordion('destroy').accordion();
	$("#toolbar").accordion( "activate" , ++size)
}

function getVizFor(analysis) {
	currentAnalysis = analysis;
	return register.getVisualizations([analysis]);
}

function getOptsFor(viz) {
	var opts = getOptions(currentAnalysis, viz);
}