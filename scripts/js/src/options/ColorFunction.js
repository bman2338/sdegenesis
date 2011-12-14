var convert = function (color) {
	return d3.rgb(255*color[0],255*color[1],255*color[2]);
}

function typeColor (graph) {
	var colors = {
		"Method": function(node) { return convert([0.2,0.8,0.2]); },
		"Class": packageColor(graph),
		"Attribute": function(node) { return convert([0.2,0.2,0.8]); },
	};
	return {
		name: "Color for node type",
		evalFun: function (node) {
				var type = node.properties.ElementType
				var func = colors[type];
				if (!func)
					func = colors["*"];
					
 				if (func.evalFun)
					return func.evalFun(node);
				else
					return func(node);
				return convert([0,0,0]);
		},
	};
}

function packageColor (graph) {
	var colors = {
	};
	var currentColor = [0.8,0.0,0.2];
	var defaultColor = [0.0,0.0,0.0];
	var classesRelation = graph.getRelation("classes");
	return {
		name: "Color for Package",
		evalFun: function (node) {
				var color = currentColor;
				var t = node.properties.ElementType;
				if (t != "Class")
					return defaultColor;
				var pkgs = graph.getRelationParent(graph.getRelation("classes"),node.uniqueId);
				
				if (pkgs.length == 0)
					return defaultColor;
				var pkgId = pkgs[0].uniqueId;
				node.properties["package"] = pkgs[0].properties.name;
					
				if (colors[pkgId])
					color = colors[pkgId];
				else {
					color = currentColor;
					colors[pkgId]  = color;
					currentColor[1] = currentColor[1] + 0.3;
				}
				return d3.rgb(255*color[0],255*color[1],255*color[2]);
		},
	};
}

function visibilityColor (graph) {
	var colors = {
		"private": convert([0.8,0.2,0.2]),
		"public": convert([0.2,0.8,0.2]),
		"protected": convert([0.8,0.8,0.1]),
		"package": convert([0.8,0.1,0.8]),
		"*": convert([0.6,0.6,0.6]),
	}
	return {
		name: "Color with respect to Visibility",
		evalFun: function (node) {
			if (node.properties.isPublic)
				return colors["public"];
			if (node.properties.isPrivate)
				return colors["private"];
			if (node.properties.isProtected)
				return colors["protected"];
			if (node.properties.isPackage)
				return colors["package"];
			return colors["*"];
		}
	}
}