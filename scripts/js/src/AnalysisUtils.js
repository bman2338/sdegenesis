function filterNodes (element, relation, graph) {
	var elementType = element;
	var relationName = relation;
	
	return {
		value: function (elements,obj) {                                                 
		var c = [];
		var nodes = elements.nodes;

		for(var n in nodes) {
			var node = nodes[n];
			var sTree = obj.source.getSubtreeByRelationName(relationName, node.uniqueId);
			if (sTree != null)
				c.push(sTree);
		}
		          return {
	                types: elements.types,
	                nodes: c,
	            };
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
	for (var n in elements.nodes) {
		if (elements.nodes[n].uniqueId == node.uniqueId)
			return true;
	}
	return false;
} 


function filterGraph (relations,vis) {
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
    
function filterMixedGraph(relations,vis) {
 	if (vis && vis.id != "Graph")
		return;
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

function filterInheritance (relations) {
	return {
		value: function (elements, obj) {
			if (obj.id == "Graph")
				return filterMixedGraph(["superclassOf"]).value(elements,obj);
			return filterNodes("Class","superclassOf").value(elements,obj);
		}
	}
}