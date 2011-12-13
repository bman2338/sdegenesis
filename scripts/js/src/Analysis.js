function getOptions(analysis,visualization) {
	var options = {};
	for (var elId in visualization.elements) {
		if (!analysis.elements[elId])
			continue;
		var elVis = visualization.elements[elId];
		var elAn = analysis.elements[elId];
		for (var anOpt in elAn.options) {
			var opts = elAn.options[anOpt];
			var visOpts = elVis.options[anOpt];
			if (!visOpts)
				elVis.options[anOpt] = {};
			if (!options[elId])
				options[elId] = {};
			options[elId][anOpt] = [];
			for (var i = 0; i < opts.length; ++i) {
				var opt = opts[i];
				options[elId][anOpt].push(opt(visualization.source));
			}
		}
	}
	return options;
}

function setUnaryOptionsAndGet (visualization,optionsArr) {
	var newOptions = {};
	for (var elId in optionsArr) {
		var element = optionsArr[elId];
		for (var anOpt in element) {
			var options = element[anOpt];
			if (options.length == 0)
				continue;
			if (options.length == 1) {
				visualization.elements[elId].options[anOpt].value = options[0];
				continue;
			}
			if (!newOptions[elId])
				newOptions[elId] = {};
			newOptions[elId][anOpt] = options;
		}
	}
	return newOptions;
}


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
