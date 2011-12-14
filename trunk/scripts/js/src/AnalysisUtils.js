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

function filterGraph (relations) {
	return {
		value: function(element,obj) {
			var centers = obj.source.getNodeSelection(function (node) {
				var indexType = element.types.indexOf(node.properties.ElementType)
				if (indexType == -1)
					return false;
				return element.nodes.indexOf(node) != -1;
			});
			var opt = {
				center : centers[0],
				oneToOneEdges : true,
				collapse : true,
				selectRelation: function (relName) { return relations.indexOf(relName) != -1; },
				};	var selection = obj.source.getSubgraph(opt);
				var subGraph = toD3Graph(selection.nodes,selection.edges);
				return {
					types: element.types,
					nodes: [subGraph],
				}
			}
		}
	}