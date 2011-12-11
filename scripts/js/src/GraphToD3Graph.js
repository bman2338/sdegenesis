//Graph to d3 graph convertion functions
function toD3SubGraph(graph, relationName, nodeType ) {
	var edges = owl.deepCopy(graph.getOneToOneEdges(relationName));
	var nodes = owl.deepCopy(graph.getNodesByType(nodeType));
	var source = nodes.length;
	var sink = source + 1;

	jQuery.each(edges, function(edgeIndex, edgeValue){
		edgeValue.source = source;
		edgeValue.target = sink;
	});

	jQuery.each(nodes, function(index, value) {
		var that = value;
		that.group = 1;
		that.name = value.properties.name;

		jQuery.each(edges, function(edgeIndex, edgeValue) {

			edgeValue.value = 1;

			if(edgeValue.from == that.uniqueId) {
				edgeValue.source = index;	
				that.group++;		
			}

			if(edgeValue.to == that.uniqueId) {
				edgeValue.target = index;
				that.group++;
			}
		});
	});

	var temp = edges;
	edges = [];
	jQuery.each(temp, function(index, edge) {
		if(edge.target != sink && edge.source != source) {
			edges.push(edge);
		}
	});
	
	return { "nodes" : nodes, "edges" : edges };
}

function toD3Trees(graph, relationName, nodeType) {
	var c = [];
	var nodes = graph.getNodesByType(nodeType);
	
	for(var n in nodes) {
		var node = nodes[n];
        c.push( graph.getSubtreeByRelationName(relationName, node.uniqueId) );
	}
	
	return { name: nodeType + " " + relationName, children: c };
	
}