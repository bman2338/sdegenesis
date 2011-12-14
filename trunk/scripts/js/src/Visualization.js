var BaseVisualization = function () {
	
	var base = {};
	base.source = undefined;
	base.visualizationName = function () { return "Tree Visualization" };
	base.name = function () {
		var visName = base.visualizationName();
		if (base.options["name"])
			visName = base.options["name"].value();
		return visName;	
	};
	base.addOption = function (optionName,option) {
		if (!base.options[optionName])
			base.options[optionName] = {};
		base.options[optionName].value = option;
	}
	base.addElementOption = function (elementName,optionName,option) {
		if (!base.elements[elementName])
			return;
		var element = base.elements[elementName];
		if (!element.options) {
			element.options = {};
		};
		if (!element.options[optionName])
			element.options[optionName] = {};
		element.options[optionName].value = option;
	};
	base.initializeFromGraph = function (graph) {
		base.source = graph;
	};
	base.options = {};
	base.elements = {};
	base.candidates = function () { return []; }
	base.allows = function (data) { return true; };
	base.augment = function (type, callback) {
		return function(element) {
		if (!base.elements[type] || !base.elements[type].options)
			return;
		var elOptions = base.elements[type].options;
		for (var optId in elOptions) {
			var option = elOptions[optId];
			if (!option.value)
				continue;
				
			/*if (option.value.preEvalFun) {
				var result = option.value.preEvalFun(element);
				if (result)
					callback(element,type,optId,result);
			}*/
			if (option.value.evalFun)
				callback(element,type,optId,option.value.evalFun);
		}
		}
	},
	base.visualize = function (element,canvas) {
		if (base.source == undefined)
			return;
		var augmentationFun = base.options["augmentationFun"].value;
		if (base.options["optFun"])
			base.options["optFun"].value(element,source);
		if (base.options["visFun"]) {
			base.options["visFun"].value(element,canvas,base,augmentationFun);
		}
		else
			throw new Exception("Vis Fun not defined for an instance of " + base.name());
	};	
	return base;
}

var TreeVisualization = function() { 
	var obj = BaseVisualization();
	obj.addOption("elementsComparator", function (a,b) {
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
	});
	obj.addOption("visFun", function (root,canvas,base,augmentationFun) { hTree(root,canvas,base,augmentationFun); });
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
				},				
			},
		},
		edges: {
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
	};
	obj.candidates = function () {
		if (obj.source == undefined)
			return [];

		var roots = [];
		if (obj.options["filter"])
			roots = obj.options["filter"].value(obj.source);
		if (roots.length == 0)
			return [];	
		
		if (obj.options["elementsComparator"])
			roots = roots.sort(obj.options["elementsComparator"].value)

		jQuery.each(roots, function() {
			var that = this;
			if(that.children) {
				if (obj.options["elementsComparator"])
					that.children = that.children.sort(obj.options["elementsComparator"].value)
			}
		});
		return roots;
	};
	return obj;
}

var SunburstVisualization = function () {
	var obj = TreeVisualization();
	obj.options["visFun"].value = function(element,canvas) {
		if (!element.children)
			_sunburst([],canvas,obj.name(),obj);
		else
			_sunburst(element.children,canvas,obj.name(),obj);
	}
	return obj;
}

var GraphVisualization = function() { 
	var obj = BaseVisualization();
	obj.addOption("visFun", function (element,canvas,base,augmentationFun) {
			forceDirectedGraph(element.nodes,element.edges,base,augmentationFun);
	});
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
			roots = obj.options["filter"].value(obj.source);
		if (roots.length == 0)
			return [];	
		
		if (obj.options["elementsComparator"])
			roots = roots.sort(obj.options["elementsComparator"].value)

		jQuery.each(roots, function() {
			var that = this;
			if(that.children) {
				if (obj.options["rootsComparator"])
					that.children = that.children.sort(obj.options["elementsComparator"].value)
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

