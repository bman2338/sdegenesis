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


function nodeTypeIncluded(node, elements) {
	var indexType = elements.types.indexOf(node.properties.ElementType)
    if (indexType == -1)
        return false;
    return elements.nodes.indexOf(node) != -1;
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
				};	
				var selection = obj.source.getSubgraph(opt);
				var subGraph = toD3Graph(selection.nodes,selection.edges);
				return {
					types: element.types,
					nodes: [subGraph],
				}
			}
		}
	}
    
function filterMixedGraph(relations) {
 
    return { 
        value : function(elements, obj) {
            var centers = obj.source.getNodeSelection(function(node) {
                return nodeTypeIncluded(node, elements ); 
            });
            
            var opt = {
                centers : centers,
                selectNode : function(node) { return elements.types.indexOf(node.properties.ElementType) != -1;  }, 
                selectEdge : function(node1, node2) { return true; },
                expand : function(node, relName) {
                    return this.selectNode(node) && relations.indexOf(relName) != -1;
                }
            };
            
            var selection = obj.source.getMixedGraph(opt);
            var mixedGraph = toD3Graph(selection.nodes, selection.edges);
          
              return {
                types: elements.types,
                nodes: [mixedGraph]
            };
        }
    };


}    