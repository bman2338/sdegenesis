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


var createAnalysis = function (name) {
	var obj = {};
	obj.name = name;
	obj.elements = {};
	obj.options = {};
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
	}
	return obj;
}

var classInheritance = function () {
	var obj = createAnalysis("Class Inheritance");
	obj.options = {
		filter: {
			name: "Filter Function",
			values: [filterNodes("Class","superclassOf")],
		},
		allowsMultipleRoots: {
			values: [true]
		}
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
				}
			}
		},
		edges: {
			
		}
	};
	obj.visualizations = [
		{ name: "Inheritance Tree", visFactory: TreeVisualization },
		{ name: "Inheritance Sunburst", visFactory: SunburstVisualization },
		//{ name: "Force-Directed Inheritance Graph", visFactory: GraphVisualization }, 
	];
	return obj;
}

function colorFun (source) {
	return {
		evalFun: function(node) {
			if (node) {
				return d3.rgb(255,0,0);
			}
			return d3.rgb(255,255,255);
		}
	}
}

function textFun (source) {
	return { 
		evalFun: function(node) {
			if (node) {
				return node[0].author;
			}
			return "";
		}
	}
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
			node.selectAll("text").text(wrapper);
			break;
	}
	}
}

var revisionHistoryAnalysis = function () {
	var obj = createAnalysis("Revision History Analysis");
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
					values: [colorFun],
				},
				textFunction: {
					name: "Entities Tooltip Text Function",
					values: [textFun],
				},
			},
		},
	};
	return obj;
}

var methodCallGraph = function () {
	var obj = createAnalysis("Call Graph");
	obj.options = {
		parametrizationFun: {
			values: [ filterGraph("invokingMethods") ],
		}
	}
	obj.elements = {
		nodes: {
			options: {
			}
		},
		edges: {
			options: {
				
			}
		}
	};
	obj.visualizations = [{
		name: "Call Graph",
		visFactory: GraphVisualization,
	}];
	return obj;
}

var analysisRegister = AnalysisRegister()
analysisRegister.addEntry(["Class"],classInheritance());
analysisRegister.addEntry(["Revision"],revisionHistoryAnalysis());
//analysisRegister.addEntry(["Class"],classCallGraph());
analysisRegister.addEntry(["Method"],methodCallGraph());
//analysisRegister.addEntry(["Class","Method"],mixedCallGraph());