function filterNodes (element, relation, graph) {
	var elementType = element;
	var relationName = relation;
	
	return {
		value: function (graph) {                                                 
		var c = [];
		var nodes = graph.getNodesByType(elementType);

		for(var n in nodes) {
			var node = nodes[n];
			var sTree = graph.getSubtreeByRelationName(relationName, node.uniqueId);
			if (sTree != null)
				c.push(sTree);
		}
		return c;
		}
	};
}

function filterNodesAndEdges(relationName, nodeType) {
	return { value: function (graph) {
		var edges = owl.deepCopy(graph.getOneToOneEdges(relationName));
		var nodes = owl.deepCopy(graph.getNodesByType(nodeType));
		return [toD3Graph(nodes, edges)];
	} };
}