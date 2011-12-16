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
	base.initialize = function (source) {
		base.source = source;
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

			if (option.value.evalFun)
				callback.value(element,type,optId,option.value.evalFun,base.source);
		}
		}
	},
	base.visualize = function (node,canvas,rootFunc) {
		if (base.source == undefined || node.nodes.length == 0)
			return;
		var augmentationFun = base.options["augmentationFun"];
		var elements = node.nodes;
		if (base.options["parametrizationFun"])
			node = base.options["parametrizationFun"].value(node,base);
			
		if (base.options["visFun"]) {
			if (node.nodes.length == 1)
				base.options["visFun"].value(node.nodes[0],canvas,base,augmentationFun);
			else {
				if (!rootFunc) {
					if (base.options["rootFun"])
						rootFunc = base.options["rootFun"].value;
					else
						return;
				}
				var newRoot = rootFunc(node.nodes,base);
				base.options["visFun"].value(newRoot,canvas,base,augmentationFun);
			}
		}
		else
			throw new Exception("Vis Fun not defined for an instance of " + base.name());
	};	
	return base;
}

var TreeVisualization = function() { 
	var obj = BaseVisualization();
	obj.id = "TreeVisualization";
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
	obj.addOption("rootFun",function toSingleRootGraph (elements,vis) {
		return { name: vis.name(), properties: { name: vis.name(), ElementType:elements[0].properties.ElementType }, children: elements };
	});
	obj.addOption("visFun", function (root, canvas, base, augmentationFun) { hTree(root, canvas, base, augmentationFun); });
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
	return obj;
}

var SunburstVisualization = function () {
	var obj = TreeVisualization();
	obj.id = "Sunburst";
	obj.options["visFun"].value = function(element,canvas,base,augmentationFun) {
		if (!element.children)
			_sunburst([],canvas,obj,augmentationFun);
		else
			_sunburst(element.children,canvas,obj,augmentationFun);
	}
	return obj;
}

var GraphVisualization = function() { 
	var obj = BaseVisualization();
	obj.id = "Graph";
	obj.addOption("visFun", function (element, canvas, base, augmentationFun) {
			forceDirectedGraph(element.nodes,element.edges,base,augmentationFun);
	});
	obj.addOption("rootFun",function (element, vis) { return element; });
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

var CalendarVisualization = function () {
	var obj = BaseVisualization();
	obj.addOption("visFun", function (element, canvas, base, augmentationFun ) {
			plotHistoryCalendar(element, base, augmentationFun);
	});
	obj.elements = {
		entries: {
			options: {
				colorFunction: {
					value: undefined,
				},
				textFunction: {
					value: undefined,
				}
			},
		}
	};
	
	obj.initialize = function (source) {
		obj.source = historyToD3Format(source);
	}
	
	obj.candidates = function () {
		if (obj.source == undefined)
			return [];
		return [source];
	};
	
	return obj;
}

var HistoryStackedBarChartVisualization = function () {
	var obj = BaseVisualization();
	obj.internalState = timelineState();
	obj.addOption("visFun", function (element, canvas, base, augmentationFun ) {
			stackedBarChart(element,canvas_width,canvas_height,null,obj.source.last,500,canvas,obj.internalState);
	});
	obj.elements = {
		bars: {
			options: {
			},
		}
	};
	obj.initialize = function (source) {
		obj.source = source;
	}
	
	obj.candidates = function () {
		if (obj.source == undefined)
			return [];
		return [source];
	};
	
	return obj;
}
