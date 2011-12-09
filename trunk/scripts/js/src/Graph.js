function ObjectId(str) {
	return str;
}


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

var cloneArray = function(array) {
	var cloned = [];
	for(var index in array) {
		cloned.push(cloneObject(array[index]));
	}
	return cloned;
}


// unsafe beware
var cloneObject = function(obj) {
	if(jQuery.isArray(obj)) {
		return cloneArray(obj);
	} else {
		var cloned = {};
		for(var key in obj) {
			cloned[key] = obj[key];
		}
		return cloned;
	}
}



var genesis = { Graph : {}};
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




	//public functions
	var graph = {
		
		initialize : function(nodes, edges) {
			this.nodes = nodes.sort(nodeComparator);
			this.edges = edges;
            this.oneToOneEdges = {};
			
			for(var e in this.edges) {
				this.edges[e] = this.edges[e].sort(adjListComparator);
				var rel = this.edges[e] ;
				for(var adjList in rel) {
					rel[adjList].to = rel[adjList].to.sort();
				}
			}
			return this;
		},
        
        
        getOneToOneEdges : function(relationName) {
            if(this.oneToOneEdges[relationName]) {
                return this.oneToOneEdges[relationName];
            }
            
            var relation =  this.getRelation(relationName);
            
            
            if(relation) {
            var oneToOneEdges = [];
            for(var adj in relation) {
                var adjList = relation[adj];
                var from = adjList.from;
                for(var e in adjList.to) {
                    var to = adjList.to[e];
                    oneToOneEdges.push({ "from": from, "to": to }); 
                }
            }
            this.oneToOneEdges[relationName] = oneToOneEdges;
            return oneToOneEdges;
            
            
            } else {
                return null;
            }
        
        },

		getRelation : function(relationName) {
			return this.edges[relationName];
		},


		getAdjList : function(relation, nodeId) {
				return ifInRangeGet(relation, binarySearch(relation, nodeId, adjListLess, adjListEquals));
			
		},

		getNodeList : function(adjList) {
			var nodeList = [];
			if(adjList) {
				for(var i = 0; i < adjList.length; i++) {
						var node = this.getNodeFromId(adjList[i]);
						if(node)
							nodeList.push(node);
				}
				return nodeList;
			}
			return [];
		},

		reset : function() {
			this.oneToOneEdges = null;
			this.nodesByType = null;
		},
		
		getNodesByType : function(type) {
			var nodeList = [];
			if(this.nodesByType) {
				return this.nodesByType;
			}
	
			for(var n in this.nodes) {
				var node = this.nodes[n];
				if(this.getNodeType(node) == type) {
					nodeList.push(node);
				}
			}
			
			this.nodesByType = nodeList;
			return nodeList;
		},

		getNodeType : function(node) {
			return node.properties.ElementType;
		},

		getNodeFromId : function(uniqueId){
			return ifInRangeGet(this.nodes, binarySearch(this.nodes, uniqueId, nodeLess, nodeEquals));
		},


		getSubtreeByRelationName : function(relationName, nodeId) {
			var relation = this.getRelation(relationName);
		if(!relation)	
		{
			return null;
		}
			var node = this.getNodeFromId(nodeId);
		if(!node) {
			return null;
		}	
			
			
			return this.getSubtree(relation, node);
		},

		getSubtree : function(relation, node) {
			var adjList = this.getAdjList(relation, node.uniqueId);
			var nodeList = [];
			if (adjList) {
				nodeList  = this.getNodeList(adjList.to);
			}
			
			var childNodes = [];
			for(var i = 0; i < nodeList.length; i++) {
				if(nodeList[i] && nodeList[i].properties.name){
					childNodes.push(this.getSubtree(relation, nodeList[i]));
				}
			}

			var obj = { name: node.properties.name, properties: node.properties, metrics: node.metrics };

			if(childNodes.length != 0)  {
				obj.children = childNodes
			}
			return obj;
		},

		}; return graph.initialize(nodes, edges)
	}