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
			if (result < 1)
			result = 1;
			return result;
		},
		name: "Number Of Total Subclasses",
	}
}
