function ObjectId(str) {
	return str;
}


function binarySearch(array, uniqueId, less, eq, fromIndex){
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

	function concat(listOfLists) {
		if(listsOfLists.length == 0) {
			return [];
		}
		
		if(listsOfList.length == 1) {
			return listOfLists[0];
		}
		
		var res = [];
		for(var ll in listOfLists) {
			var list = listOfLists[ll];
			for(var l in list) {
				res.push(list[l]);
			}
		}
		return res;
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

	var graph = {
		
		//public functions
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
			//TODO test it and eventually subsitute the code on the above method
			getOneToOneEdgesFromAdjList : function(adjLists) {
				var relation =  adjLists;

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
        

        // select(node) -> boolean
        getNodeSelection : function(select) {
			var nodeList = [];
			for(var n in this.nodes) {
				var node = this.nodes[n];
				if(select(node)) {
					nodeList.push(node);
				}
			}
			
			return nodeList;
        },
        
        
                
        getNodeFromListFromId : function(nodeList, uniqueId)  {
            return ifInRangeGet(nodeList, binarySearch(nodeList, uniqueId, nodeLess, nodeEquals));
        
        },

		getSubgraphAux : function(adjList, adjLists, relation, connected) {
			if(!adjList) {
				return;
			}
			
			var toCheck = [adjList];
			
			while (toCheck.length != 0) {
				var check = toCheck[0];
				toCheck = toCheck.slice(1);
				
				if (!connected[check.from]) {
					connected[check.from] = true;
					adjLists.push(check);
					
					for (var toAdj in check.to) {
						var toAdjList = this.getAdjList(relation,check.to[toAdj]);
						if (toAdjList != null) {
							toCheck.push(toAdjList);
						} else {
                            connected[check.to[toAdj]] = true;
                        }
					}
				}
			}
			
		},
        

		/**
		* opt.center : the node from which the graph is built.
		* opt.selectRelation : (relName) -> boolean, the relation to include in the selection.
		* opt.oneToOneEdges : boolean, true if an array of edges of the form { from, to } are required
		* else it will return an array of adjLists.
		* return { nodes:  { relName : [ nodes* ] },  (edges|adjLists) : { relName : [ (edges|adjLists)* ] }  }
		*/ 
        getSubgraph : function(opt) {
			if(!opt.center) return null;
			
			var center = opt.center;
			var adjLists = {};
			var nodes = {};
			
			for(var r in this.edges) {
				var relation = this.edges[r];
				if(opt.selectRelation(r)) {
					adjLists[r] = [];
					var connected = {};
					
					var adjList = this.getAdjList(relation, center.uniqueId);
					this.getSubgraphAux(adjList, adjLists[r], relation, connected);
					nodes[r] = this.getNodesFromIdSet(connected);
				}
			}
			
			if(opt.oneToOneEdges) {
				var edges = {};
				for(var r in adjLists) {
					edges[r] = this.getOneToOneEdgesFromAdjList(adjLists[r]);
				}
				
				return { "nodes" : nodes, "edges" : edges };
				
			}
			
			return { "nodes" : nodes, "adjLists" : adjLists };
		},
		
		
		getNodesFromIdSet: function(idSet) {
			var nodes = [];
			for(var id in idSet) {
                var node = this.getNodeFromId(id);
                if(node) {
                    nodes.push(node);
                }
			}
			return nodes;
		},
        
     
        /**
        * opt.selectEdge : (node1, node2) -> boolean
        * opt.selectRelation : (relationName) -> boolean
        * opt.selectNode : (node) -> boolean
        * opt.removeUnconnected : Boolean
        * returns { nodes: [node*], edges: [ { rel : [ {from, to}* ] }* ] }
        *
        * Example usage of getSelection(opt);
            
        	getSelection({
                removeUnconnected : true,
                selectRelation : function(rel) { return rel == anExampleRelationName; },
                selectNode : function(node) {   return node.properties.ElementType == anExampleNodeType; },
                selectEdge : function(nodeFrom, nodeTo) { return true; }
			});
        */
        getSelection : function(opt) {
            
            if(!opt.selectEdge) {
                opt.selectEdge = function(x, y) { return true; };
            } 
            if(!opt.selectRelation) {
                opt.selectRelation = function(x) { return true; };
            } 
            if(!opt.selectNode) {
                opt.selectNode = function(x) { return true; };
            } 
            
            if(opt.removeUnconnected) {
                opt.connected = {};
            }
            
            var nodeSelection = [];
            for(var n in this.nodes) {
                var node = this.nodes[n];
                if(opt.selectNode(node)) {
                    nodeSelection.push(node);
                }
            }
            
        
        
        
           edgeSelection = [];
           for(var rel in this.edges) {
                if(opt.selectRelation(rel)) {
                    edgeSelection[rel] = (this.getEdgeSelectionFromRelation(opt, nodeSelection, this.edges[rel]));
                }
           }
           
           if(opt.removeUnconnected) {
            var temp = nodeSelection;
            nodeSelection = [];
            for(var n in temp) {
                var id = temp[n].uniqueId;
                if(opt.connected[id]) {
                    nodeSelection.push(temp[n]);
                }
            }
            
           }
           opt.connected = {};
            return { "nodes" : nodeSelection, "edges" : edgeSelection };
        },
        


        getEdgeSelectionFromRelation : function(opt, nodeSelection, relation) {
            selection = [];
            for(var r in relation) {
                var adjList = relation[r];
                var from = adjList.from;
                var fromNode = this.getNodeFromListFromId(nodeSelection, from);
                if(!fromNode) {
                    continue;
                }
                
                for(var adjTo in adjList.to) {
                    var to = adjList.to[adjTo];
                    var toNode = this.getNodeFromListFromId(nodeSelection, to);
                    if(!toNode) {
                        continue;     
                    }
                    
                   
                    
                    if(opt.selectEdge(fromNode, toNode)) {
                        selection.push({ "from" : from, "to" : to });
                         if(opt.removeUnconnected) {
                            opt.connected[from] = true;
                            opt.connected[to] = true;
                    }
                    }
                }
            }   
            return selection;
        },
        
        
		getNodesByType : function(type) {
			var nodeList = [];
			if(this.nodesByType && this.nodesByType[type]) {
				return this.nodesByType[type];
			}

			for(var n in this.nodes) {
				var node = this.nodes[n];
				if(this.getNodeType(node) == type) {
					nodeList.push(node);
				}
			}
			if(!this.nodesByType) {
				this.nodesByType = {};
			}

			this.nodesByType[type] = nodeList;
			return nodeList;
		},

		getNodeType : function(node) {
			return node.properties.ElementType;
		},

		getNodeFromId : function(uniqueId){
			return ifInRangeGet(this.nodes, binarySearch(this.nodes, uniqueId, nodeLess, nodeEquals));
		},
		
		getNodes: function () {
			return nodes;
		},

		getAdjLists: function () {
			return edges;
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
