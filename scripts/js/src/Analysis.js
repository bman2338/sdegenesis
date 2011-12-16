function getOptions(analysis,visualization) {
	var options = {};
	for (var elId in visualization.elements) {
		if (!analysis.elements[elId])
			continue;
		var elVis = visualization.elements[elId];
		var elAn = analysis.elements[elId];
		for (var anOpt in elAn.options) {
			var opt = elAn.options[anOpt];
			var visOpts = elVis.options[anOpt];
			if (!visOpts)
				elVis.options[anOpt] = {};
			if (!options[elId])
				options[elId] = {};
			options[elId][anOpt] = opt;
		}
	}
	for (var optId in analysis.options) {
		var opt = analysis.options[optId];
		if (!options["#general"])
			options["#general"] = {};
		options["#general"][optId] = opt;
	}
	return options;
}

function setUnaryOptionsAndGet (visualization,optionsArr) {
	var newOptions = {};
	for (var elId in optionsArr) {
		var element = optionsArr[elId];
		for (var anOpt in element) {
			var options = element[anOpt];
			if (options.values.length == 0)
				continue;
			if (options.values.length == 1) {
				if (elId == "#general") 
					visualization.addOption(anOpt,options.values[0].value);
				else
					visualization.elements[elId].options[anOpt].value = options.values[0](visualization.source);
				continue;
			}
			if (!newOptions[elId])
				newOptions[elId] = {};
			newOptions[elId][anOpt] = options;
		}
	}
	return newOptions;
}

function bootstrapVisualization (analysis,index,source) {
	var vis = analysis.instantiateVisualization(index);
	vis.initialize(source);
	var opts = getOptions(analysis,vis);
	opts = setUnaryOptionsAndGet(vis,opts);
	vis.availableOptions = opts;
	return vis;
}


function AnalysisRegister () {
	var registry = {};
	var obj = {};
	
	obj.addEntry = function (elementTypes,analysis) {
		var types = elementTypes.slice().sort();
		if (registry[types]) {
			registry[types].push(analysis);
		}
		else {
			registry[types] = [analysis];
		}
	}
	obj.getEntries = function (elementTypes) {
		var types = elementTypes.slice().sort();
		if (registry[types])
			return registry[types];
		return [];
	}
	return obj;
}


var defaultAugmentation = {
	value: function (node,type,resultType,result) {
		switch (resultType) {
			case "colorFunction":
			node.style("fill",result);
			break;
		case "sizeFunction":
			node.attr("r",result);
			break;
		case "mouseover":
			node.on("mouseover", result);	
			break;
		case "mouseout":
			node.on("mouseout", result);
			break;
		case "strokeFunction":
			node.style("stroke",result);
			break;
		}
	}
}



var createAnalysis = function (name) {
	var obj = {};

	obj.name = name;
	obj.elements = {};
	obj.options = {
		augmentationFun: {
			name: "Default Augmentation",
			values: [defaultAugmentation],
		}
	};
	obj.visualizations = [];
	obj.instantiateVisualization = function (index) {
		if (index >= obj.visualizations.length || index < 0)
			return null;
		return obj.visualizations[index].visFactory();
	};
	obj.getVisualization = function (index) {
		if (index >= obj.visualizations.length || index < 0)
			return null;
		return obj.visualizations[index];
	};
	obj.getVisualizations = function (roots) {
		return obj.visualizations;
	};
	return obj;
}

var mouseOut = function(revEntries) {
	return {
		evalFun: function(entries) {
			tooltip.hide()
		}
	};
}

function mouseOverNode () {
	return {
		evalFun: function (node) {
			tooltip.show(createInfo(node));
		}
	}
}


var classInheritance = function () {
	var obj = createAnalysis("Class Inheritance");
	obj.options["parametrizationFun"] = {
		values: [ filterInheritance() ],
	};
	obj.elements = {
		nodes: {
			options: {
				sizeFunction: {
					name: "Node Size Meaning",
					values: [classSize,childrenSize],
				},
				colorFunction: {
					name: "Node Color Meaning",
					values: [typeColor],
				},
				mouseover: {
					values: [ mouseOverNode ]
				},
				
				mouseout: {
					values: [ mouseOut ]
				},
			}
		},
		edges: {
			
		}
	};
	obj.visualizations = [
		{ name: "Inheritance Tree", visFactory: TreeVisualization },
		{ name: "Inheritance Sunburst", visFactory: SunburstVisualization },
		{ name: "Force-Directed Inheritance Graph", visFactory: GraphVisualization }, 
	];
	return obj;
}

var calendarAugmentationFun = {
	value: function (node,type,resultType,result,source) {

	var wrapper = function (node) {
		var format = d3.time.format(getHistoryDateFormatString());
		var fd = format(node);
		var array = source.data[fd];
		return result(array);
	}
	
	switch (resultType) {
		case "colorFunction":
			node.style("fill",wrapper);
			break;
		case "textFunction":
			//node.selectAll("text").text(wrapper);
			break;
		case "mouseover":
			node.on("mouseover", wrapper);	
			break;
		case "mouseout":
			node.on("mouseout", wrapper);
			break;	
 		}
	}
}

var authorMouseOverFunction = function(revEntries) {
	return {
		evalFun: function(entries) {
			if(!entries)
				return;
			tooltip.show(getAuthorListStr(entries))	
				
		}
	};
}

var getAuthorListStr = function(entries) {
	var str = "";
	var authors = {};
	for(var e in entries) {
		var entry = entries[e];
		if (!authors[entry.author])
			authors[entry.author] = getAuthorContributionValue(entry);
		else
			authors[entry.author] += getAuthorContributionValue(entry);//entry.date;
	}
	
	var authorsList = [];
	for (var a in authors) {
		authorsList.push({
			name: a,
			rank: authors[a],
		});
	}
	
	authorList = authorsList.sort(function (a,b) { 
		var ret = b.rank - a.rank;
		if (ret == 0) {
			return a.name > b.name ? -1 : 1;
		}
		return ret;
	})
	
	for(var a in authorList) {
		str += getAuthorLabel(authorList[a].name);
	}
	
	return str;


var getAuthorListStrFunction = function(revEntries) {
	return {
		evalFun : getAuthorListStr
		
		}
	};
}


var revisionHistoryAnalysis = function () {
	var obj = createAnalysis("Repository Activity Author Analysis");
	obj.allowsMultipleRoots = false;
	obj.options = {
		augmentationFun: {
			name: "Augmentation Function for Calendar",
			values: [calendarAugmentationFun],
		}
	};
	obj.elements = {
		entries: {
			options: {
				colorFunction: {
					name: "Entities Color Function",
					values: [authorColorFunction],
				},
				mouseover: {
					name: "Show the developers which committed that day",
					values: [ authorMouseOverFunction ]
				},
				
				mouseout: {
					values: [ mouseOut ]
				},
				
			},
		},
	};
	obj.visualizations = [
		{name:"Calendar View", visFactory:CalendarVisualization}
	]
	return obj;
}

var activityDensityAnalysis = function () {
	var obj = revisionHistoryAnalysis();
	obj.name = "Activitiy Density Analysis";
	obj.options["parametrizationFun"] = {
		values: [historyDensityCalculation()],
	};
	obj.elements["entries"].options["colorFunction"].values = [densityColorFunction];
	obj.visualizations = [
		{name:"Calendar Intensity View", visFactory:CalendarVisualization}
	];
	return obj;
}

var methodCallGraph = function () {
	var obj = createAnalysis("Call Graph");
	obj.options["parametrizationFun"] = {
		values: [ filterCallGraph() ],
	};
	obj.elements = {
		nodes: {
			options: {
				sizeFunction: {
					name: "Size of the nodes",
					values: [invocationsSize],
				},
				colorFunction: {
					name: "Color of the nodes",
					values: [visibilityColor],
				},
				strokeFunction: {
					name: "Stroke of the nodes",
					values: [finalStroke],
				},
				mouseover: {
					values: [ mouseOverNode ]
				},
				
				mouseout: {
					values: [ mouseOut ]
				},
			}
		},
		edges: {
			options: {
				
			}
		}
	};
	obj.visualizations = [
	{
		name: "Call Tree",
		visFactory: TreeVisualization,
	},
	{
		name: "Call Sunburst",
		visFactory: SunburstVisualization,
	},
	{
		name: "Call Graph",
		visFactory: GraphVisualization,
	},
	];
	return obj;
}


var mixedCallGraph = function() {
    var obj = methodCallGraph();
    obj.name = "Mixed Call Graph";
    obj.options.parametrizationFun.values = [ filterMixedGraph([ "methods", "invokingMethods", "parentType" ], { type: "Class", rel: "methods" }) ];
    obj.elements.nodes.options.colorFunction.values = [ typeColor ];
    obj.elements.nodes.options.sizeFunction.values = [  mixedMethodInvocAndClassMethods ];
	obj.visualizations = [
	{
		name: "Call Graph",
		visFactory: GraphVisualization,
	}];
    return obj;
};

var authorsCollaborationGraph = function () {
	var obj = createAnalysis("Authors Collboration");
	obj.options["parametrizationFun"] = {
		values: [ transformHistoryToAuthorCollaboration() ],
	};
	obj.elements = {
		nodes: {
			options: {
				colorFunction: {
					values:[authorGraphColorFunction],
				},
				sizeFunction: {
					values:[collaborationsSizeFunction],
				},
				mouseover: {
					values: [ mouseOverAuthorNode ]
				},
				
				mouseout: {
					values: [ mouseOut ]
				},
			}
		}
	};
	obj.visualizations = [
	{
		name: "Collaboration Graph",
		visFactory: GraphVisualization,
	}
	];
	return obj;
};

var timelineStackedBarChart = function () {
	var obj = createAnalysis("Revisions Timeline");
	obj.visualizations = [
	{
		name: "Contributions BarChart View",
		visFactory: HistoryStackedBarChartVisualization,
	}];
	return obj;
}

var analysisRegister = AnalysisRegister()
analysisRegister.addEntry(["Class"],classInheritance());
analysisRegister.addEntry(["Author"],authorsCollaborationGraph());
analysisRegister.addEntry(["Author"],revisionHistoryAnalysis());
analysisRegister.addEntry(["Revision"],activityDensityAnalysis());
analysisRegister.addEntry(["Method"],methodCallGraph());
analysisRegister.addEntry(["Method", "Class" ],mixedCallGraph());
analysisRegister.addEntry(["Revision"],timelineStackedBarChart());
