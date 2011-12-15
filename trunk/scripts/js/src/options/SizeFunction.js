function elementSize (graph) {
	
	var funcs = {
		"Class": classSize(graph),
		"Method": function(node) { return 5; },
		"*": function(node) { return 3; },
	}
	
	return {
		evalFun: function (node) {
			var func = funcs[node.properties.ElementType];
			if (func) {
				if (func.evalFun)
					return func.evalFun(node);
				else
					return func(node);
			}
			else
				return funcs["*"](node);
		},
		name: "Element Size"
	}	
}

function classSize (graph) {
	return {
		evalFun: function (node) {
			var methods = getMetric(node,"numberOfMethods",function() { 
				var res = graph.getAdjList(graph.getRelation("methods"),node.uniqueId);
				return res == null ? 0 : res.to.length;
			});
			var attributes = getMetric(node,"numberOfAttributes",function () { 
				var res = graph.getAdjList(graph.getRelation("attributes"),node.uniqueId);
				return res == null ? 0 : res.to.length;
			});
			var res = methods + attributes;
			if (res < 1)
				res = 1;
			if (res > 50)
				res = 50;
			return res;
		},
		name: "Class Size"
	}
}

function childrenSize (graph) {
	return {
		evalFun: function (node) {
			var result = getMetric(node,"numberOfTotalSubclasses",function () {
				var total = 0;
				var toCheck = [node];
				while (toCheck.length != 0) {
					var check = toCheck[0];
					toCheck = toCheck.slice(1);
					var res = graph.getAdjList(graph.getRelation("superclassOf"),check.uniqueId);
					if (res != null) {
						total = total + res.to.length;
						var list = graph.getNodeList(res.to);
						for (var i = 0; i < list.length; ++i) {
							toCheck.push(list[i]);
						}
					}	
				}
				return total;
			});
			if (result < 2)
				result = 2;
			if (result > 50)
				result = 50;
			return result;
		},
		name: "Number Of Total Subclasses",
	}
}

// Methods

function invocationsSize (graph) {
	return {
		evalFun: function (node) {
			var result = graph.getAdjList(graph.getRelation("invokingMethods"),node.uniqueId);
			if (result)
				return Math.min(50,Math.max(result.to.length*5,5));
			return 5;
		},
		name: "Number of Performed Invocations"
	}
}



// Mixed

function mixedMethodInvocAndClassMethods(graph) {
    var inv = invocationsSize(graph);
    var clazz = classSize(graph);
    
    var sizeFun = { 
        "Method" : inv.evalFun,
        "Class" :   clazz.evalFun
    };
    
    return {
        evalFun: function(node) {
            var sf =  sizeFun[node.properties.ElementType];
            if(!sf) return 2; //Not going to happen but you never know
            return sf(node);
        }, 
        
        name: inv.name + " " + clazz.name
    };
    
}


