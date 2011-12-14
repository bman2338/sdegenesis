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


var createAnalysis = function (name,filterFun) {
	var obj = {};
	obj.name = name;
	obj.elements = {};
	obj.options = {
		filter: {
			name: "Filter Function",
			values: [filterFun],
		}
	};
	return obj;
}

var classInheritance = function () {
	var obj = createAnalysis("Class Inheritance",filterNodes("Class","superclassOf"));
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
	}
	return obj;
}

var analysisRegister = AnalysisRegister()
analysisRegister.addEntry(["Class"],classInheritance());

var inheritanceHierarchyAnalysis = {
	name: "Inheritance Hierarchy",
	options: { 
		elementType: "Class",
		relation: "superclassOf",
	},
	elements: {
		nodes: {
			options: {
				sizeFunction: [classSize,childrenSize],
				colorFunction: [typeColor],
			},
		},
		edges: {
			options: {
				asd: [typeColor],
			},
		}
	}
};

var callGraph = {
	name: "Call Graph",
	options: {
		elementType: "Method",
		relation: "Call",
	}
}

var revisionRelatedChanges = {
	name: "Revision Related Changes",
	options: {
		elementType: "Class",
		relation: "Revision",
	}
}

var bugAuthorRelation = {
	name: "Bug-Author Relationship",
	options: {
		elementType: "Bug",
		relation: "Author",
	}
}

var entityAuthorRelation = {
	name: "Entity-Author Relationship",
	options: {
		elementType: "Class",
		relation: "Author",
	}
}

var genericGraph = {
	name: "Generic Graph",
	options: {
		elementType: "",
		relation: "",
	}
}
