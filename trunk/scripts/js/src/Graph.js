function ObjectId(str) {
	return str;
}


genesis = { Graph : {}};
genesis.Graph.create = function(nodes, edges) {
	//private functions
	var nodeComparator = function(node1, node2) {
		return node1.uniqueId - node2.uniqueId;
	};
	
	var adjListComparator = function(adjList1, adjList2) {
		return adjList1.from - adjList2.from;
	};
	
	//Helper for binary search
	var nodeLess = function(node, nodeId) {
		return node.uniqueId < nodeId;
	};
	
	var nodeEquals = function(node, nodeId) {
		return node.uniqueId == nodeId;
	};

	var adjListLess = function(adjList, fromId) {
		return adjList.from < fromId;
	};
	
	var adjListEquals = function(adjList, fromId) {
		return adjList.from == fromId;
	};

	var ifInRangeGet = function(array, index) {
		if(index < array.length) {
			return array[index];
		} else {
		return null
		}
	};

	var binarySearch = function(array, uniqueId, less, eq, fromIndex){
		var left = 0;
		if(fromIndex) {
			left = fromIndex;
		}
		
		var right = array.length - 1;
		while (left <= right){
			var mid = parseInt((left + right)/2);
			if (eq(array[mid], uniqueId)) {
				return mid;
			}
			else if (less(array[mid], uniqueId)) {
				left = mid + 1;
			}
			else {
				right = mid - 1;
			}
		}
		
		return array.length;
	}


	//public functions
	var graph = {
		
		initialize : function(nodes, edges) {
			this.nodes = nodes.sort(nodeComparator);
			this.edges = edges;
			
			for(var e in this.edges) {
				this.edges[e] = this.edges[e].sort(adjListComparator);
				var rel = this.edges[e] ;
				for(var adjList in rel) {
					rel[adjList].to = rel[adjList].to.sort();
				}
			}
			return this;
		},

		getRelation : function(relationName) {
			return this.edges[relationName];
		},


		getAdjList : function(relation, nodeId) {
			// var adjList = null;
			// for(var i = 0; i < relation.length; i++) {
			// 	if(relation[i].from == nodeId) {
			// 		adjList = relation[i];
			// 	}
			// }
			// return adjList;
			
				return ifInRangeGet(relation, binarySearch(relation, nodeId, adjListLess, adjListEquals));
			i
		},

		getNodeList : function(adjList) {
			var nodeList = [];
			if(adjList) {
				//var startIndex = 
				for(var i = 0; i < adjList.length; i++) {
				//	for(var j = 0; j < nodes.length; j++) {
					//	if(adjList.to[i] == this.nodes[j].uniqueId) {
						var node = this.getNodeFromId(adjList[i]);
						if(node)
							nodeList.push(this.nodes[j]);
				}
				return nodeList;
			}
			return [];
		},



		getNodeFromId : function(uniqueId){
			// for(var i = 0; i < nodes.length; i++){
			// 		if(this.nodes[i].uniqueId == uniqueid){
			// 			return this.nodes[i];
			// 		}
			// 	}
			
			return ifInRangeGet(this.nodes, binarySearch(this.nodes, uniqueId, nodeLess, nodeEquals));
		},


		getSubtreeByRelationName : function(relationName, nodeId) {
			var relation = this.getRelation(relationName);
			if(!relation) {
				alert("PORC")
			}
			
			var node = this.getNodeFromId(nodeId);
			return this.getSubtree(relation, node);
		},

		getSubtree : function(relation, node) {
			var adjList = this.getAdjList(relation, node.uniqueId);
			var nodeList = this.getNodeList(this.nodes, adjList);

			var childNodes = [];
			for(var i = 0; i < nodeList.length; i++) {
				if(nodeList[i] && nodeList[i].properties.name){
					childNodes.push(this.getSubtree(relation, nodeList[i]));
				}
			}

			if(childNodes.length == 0)  {
				return { name: node.properties.name }
			}
			return {  name: node.properties.name,  children:  childNodes };
		},

		}; return graph.initialize(nodes, edges)
	}