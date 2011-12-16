function sortNodes(a, b) {
    if(b.children) {
        if(a.children) {
            var res = b.children.length - a.children.length;
            if(res == 0) {
                res = (a.name < b.name)? -1 : 1;
            }
            return res;
        } else  return 1;
    }
    else return -1;
}

function invSortNodes(a, b) {
    return sortNodes(a, b);
}


function roots(data) {
    var title = data.name;
    var target = "#chart";
    var rootsTarget = "#roots";
    var titleTarget = "#treename";
    d3.select(titleTarget).html(title);
    d3.select(rootsTarget).html("");
    
    var json = data.children;
    var el = d3.select(rootsTarget);
    
    json = json.sort(sortNodes);
    
    
    el = el.append("list");
    jQuery.each(json, function() {
        var that = this;
        if(that.children) {
            that.children = that.children.sort(invSortNodes);
        }
        
        
        var callback = function(ev) { 
            d3.select("#monitor").html(that.name) 
            hTree(that, target) 
        };
        
        el.append("li").html("a").html(that.name).on("click", 
                                                     callback
                                                     );//.on("mouseover", callback);
            
    });
    d3.select("#monitor").html(json[0].name)
        hTree(json[0], target);
    
}

function isBranch(d) {
    return (d.children && d.children.length > 1) 
}

function hTree(root, target,visModel,augmentationCallback) {
    d3.select(target).html("");
    var json = root;

	var width = canvas_width;
	var height = canvas_height;
    
	var offsetX = width / 2.0 ;
	var offsetY = height / 2.0;
    var r = height/2;
    var tree = d3.layout.tree()
		.size([360, r - 120])
		.separation(function(a,b) { return (a.parent == b.parent ? 1: 2)/a.depth });
    
    var diagonal = d3.svg.diagonal.radial()
		.projection(function(d) { return [ d.y, d.x /180 * Math.PI]; });
    
    
    
    var vis = d3.select(target)
		.append("svg:svg")
		.attr("width", width)
		.attr("height", height)
		.append("svg:g")
		.attr("transform", "translate(" + (offsetX )+ "," + (offsetY ) + ")");
    
    var nodes = tree.nodes(json);
    var link = vis.selectAll("path.link")
		.data(tree.links(nodes))
		.enter().append("svg:path")
		.attr("class", "link")
		.attr("d", diagonal);
    
    var node = vis.selectAll("g.node")
		.data(nodes)
		.enter().append("svg:g")
		.attr("class", function(d) { var clazz = "node"; if(d === root) { return " Root"; } if(isBranch(d)) { return " Branch"; } return clazz;})
		.attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")" });
		// .attr("transform", function(d) { return "scale(" + 1 + ")translate(" + 1 + ")" });
       
        
	node.append("svg:circle")
		.attr("r", function(d) {
			if(d.children)
			return d.children.length/5 + 4; 
			return  4; })
			.on("mouseover", function(d, i) {  
				 tooltip.show(createInfo(d));
			})
			.on("mouseout", function(d) {
				tooltip.hide();
			})   
			.on("click", function(d, i) {
				if(isBranch(d)) {
					if(d.hParent) {
						hTree(d.hParent, target,visModel,augmentationCallback);
						d.hParent = null;
					} else {
						d.hParent = root;
						hTree(d, target,visModel,augmentationCallback) 
					}
				}
			});

		if (augmentationCallback)
			node.select("circle").call(visModel.augment("nodes",augmentationCallback));

}
