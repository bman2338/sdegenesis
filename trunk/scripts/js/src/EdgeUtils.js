function getRelation(edges, relationName) {
	return edges[relationName];
}

function getAdjList(relation, nodeId) {
	var adjList = null;
	for(var pair in relation) {
		if(pair.from == nodeId) {
			adjList = pair;
		}
	}
	return adjList;
}

function getNodeList(nodes, adjList) {
	var nodeList = [];
	var i=0;
	for(var nodeId in adjList.to) {
		for(var node in nodes) {
			if(nodeId == node.uniqueId) {
				nodeList[i++] = node;
			}
		}
	}
	return nodeList;
}

function getSubtreeByRelationName(nodes, edges, relationName, nodeId) {
	relation = getRelation(edges, relationName);
	node = getNodeById(nodes, nodeId);
	return getSubtree(nodes, edges, relation, node);
}

function getSubtree(nodes, edges, relation, node) {
	var adjList = getAdjList(relation, node.uniqueId);
	var nodeList = getNodeList(nodes, adjList);
	
	childNodes = [];
	i = 0;
	for(var child in nodeList) {
		childNodes[i++] = getSubtree(nodes, edges, relation, child);
	}
	
	
	return {  name: node.properties.name,  children:  childNodes };
}




