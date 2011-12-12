var BaseVisualization = function () {
	
	var base = {};
	base.source = undefined;
	base.visualizationName = function () { return "Tree Visualization" };
	base.name = function () {
		var visName = base.visualizationName();
		if (base.options["name"])
			visName = base.options["name"]();
		return visName;	
	}	
	base.initializeFromGraph = function (graph) {
		base.source = graph;
	};
	base.options = {};
	base.elements = {};
	base.candidates = function () { return []; }
	base.allows = function (data) { return true; };
	base.visualize = function (element,canvas) {
		if (base.source == undefined)
			return;
	
		if (base.options["optFun"])
			base.options["optFun"](element,source);
		if (base.options["visFun"])
			base.options["visFun"](element,canvas);
		else
			throw new Exception("Vis Fun not defined for an instance of " + base.name());
	};	
	return base;
}

var TreeVisualization = function() { 
	var obj = BaseVisualization();
	obj.options.elementsComparator = function (a,b) {
			if(b.children) {
				if(a.children) {
					var res = b.children.length - a.children.length;
					if(res == 0) {
						res = (a.name < b.name)? -1 : 1;
					}
					return res;
				} 
				else  
					return 1;
			}
			else 
				return -1;
		};
	obj.options.visFun = function (root,canvas) { hTree(root,canvas); };
	obj.elements = {
		nodes: {
			options: {
				sizeFunction: {
					value: undefined,
					type: "Metric",
				},
				colorFunction: {
					value: undefined,
					type: "Metric",
				}
			},
		},
		edges: {
			sizeFunction: {
				value: undefined,
				type: "Metric",
			},
			colorFunction: {
				value: undefined,
				type: "Metric",
			}
		},
	};
	obj.candidates = function () {
		if (obj.source == undefined)
			return [];

		var roots = [];
		if (obj.options["filter"])
			roots = obj.options["filter"](obj.source);
		if (roots.length == 0)
			return [];	
		
		if (obj.options["elementsComparator"])
			roots = roots.sort(obj.options["elementsComparator"])

		jQuery.each(roots, function() {
			var that = this;
			if(that.children) {
				if (obj.options["elementsComparator"])
					that.children = that.children.sort(obj.options["elementsComparator"])
			}
		});
		return roots;
	};
	return obj;
}

var SunburstVisualization = function () {
	var obj = TreeVisualization();
	obj.options["visFun"] = function(element,canvas) {
		if (!element.children)
			_sunburst([],canvas,obj.name());
		else
			_sunburst(element.children,canvas,obj.name());
	}
	return obj;
}

var GraphVisualization = function() { 
	var obj = BaseVisualization();
	obj.options.visFun = function (element,canvas) {
			forceDirectedGraph(element.nodes,element.edges);
		};
	obj.elements = {
		nodes: {
			options: {
				sizeFunction: {
					value: undefined,
					type: "Metric",
				},
				colorFunction: {
					value: undefined,
					type: "Metric",
				}
			},
		},
		edges: {
			sizeFunction: {
				value: undefined,
				type: "Metric",
			},
			colorFunction: {
				value: undefined,
				type: "Metric",
			}
		},
	};
	obj.candidates = function () {
		if (obj.source == undefined)
			return [];

		var roots = [];
		if (obj.options["filter"])
			roots = obj.options["filter"](obj.source);
		if (roots.length == 0)
			return [];	
		
		if (obj.options["elementsComparator"])
			roots = roots.sort(obj.options["elementsComparator"])

		jQuery.each(roots, function() {
			var that = this;
			if(that.children) {
				if (obj.options["rootsComparator"])
					that.children = that.children.sort(obj.options["elementsComparator"])
			}
		});
		return roots;
	};
	return obj;
}


function filterNodes (element, relation, graph) {
	var elementType = element;
	var relationName = relation;
	return function (graph) {                                                 
		var c = [];
		var nodes = graph.getNodesByType(elementType);

		for(var n in nodes) {
			var node = nodes[n];
			c.push( graph.getSubtreeByRelationName(relationName, node.uniqueId) );
		}
		return c;
	}
}

