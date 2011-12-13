function getOptions(analysis,visualization) {
	var options = {};
	for (var elId in visualization.elements) {
		if (!analysis.elements[elId])
			continue;
		var elVis = visualization.elements[elId];
		var elAn = analysis.elements[elId];
		for (var visOpt in elVis.options) {
			var opts = elAn.options[visOpt];
			if (opts) {
				if (!options[elId])
					options[elId] = {};
				options[elId][visOpt] = [];
				for (var i = 0; i < opts.length; ++i) {
					var opt = opts[i];
					options[elId][visOpt].push(opt(visualization.source));
				}
			}
		}
	}
	return options;
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
			},
		},
		edges: {
			
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
