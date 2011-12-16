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
    
function filterMixedGraph(relations) {
    return { 
        value : function(elements, obj) {
			if (obj && obj.id != "Graph")
				return elements;
	
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

function filterCallGraph () {
	return {
		value: function (elements,obj) {
			if (obj.id == "Graph")
				return filterMixedGraph(["invokingMethods"]).value(elements,obj);
			return filterNodes("Method","invokingMethods",obj.source).value(elements,obj);
		}
	}
}

function filterInheritance () {
	return {
		value: function (elements, obj) {
			if (obj.id == "Graph")
				return filterMixedGraph(["superclassOf"]).value(elements,obj);
			return filterNodes("Class","superclassOf").value(elements,obj);
		}
	}
}


function transformHistoryToAuthorCollaboration () {
	var matrix = null;
	return {
		value: function (elements,obj) {
			if (!matrix)
				matrix = getCoauthorshipMatrix(obj.source);
			var nodes = [];
			var collaborations = [];
			for (var author in matrix) {
				if (author == "__maxVal")
					continue;
				var authorNode = {
					name: author,
					uniqueId: author,
					numberOfActions: matrix[author][author],
					properties: {
						name: author
					},
				};
				for (var other in matrix[author]) {
					if (other == "__maxVal" || other == author)
						continue;
					if (matrix[author][other]) {
						var collaboration = {
						 	from: author,
							to: other,
							value: matrix[author][other]/2.0,
						};
						collaborations.push(collaboration);
					}
				}
				nodes.push(authorNode);
			}
			return {
				types: elements.types,
				nodes: [toD3Graph(nodes,collaborations)],
			}
		}
	}
}

function historyDensityCalculation () {
	return {
		value: function (element,vis) {
			var history = vis.source;
			var max = 0;
			for (var date in history.data) {
				var entries = history.data[date];
				var value = 0;
				for (var e in entries) {
					var entry = entries[e];
					value += getAuthorContributionValue(entry);
				}
				entries[0].dayContributionValue = value;
				max = Math.max(value,max);
			}
			for (var date in history.data) {
				var entries = history.data[date];
				entries[0].dayContributionValue = entries[0].dayContributionValue/(1.0*max);
			}
			return {
				types: element.types,
				nodes: [history],
			};
	}
}
}