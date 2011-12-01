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
    return -sortNodes(a, b);
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

function hTree(root, target) {
    d3.select(target).html("");
    var json = root;
    //	d3.json("data3.json", 
	
    //	function(json) {
    
    var offset = -10;
    var r = 900/2;
    var tree = d3.layout.tree()
		.size([360, r - 120])
		.separation(function(a,b) { return (a.parent == b.parent ? 1: 2)/a.depth });
    
    var diagonal = d3.svg.diagonal.radial()
		.projection(function(d) { return [ d.y, d.x /180 * Math.PI]; });
    
    
    
    var vis = d3.select(target)
		.append("svg:svg")
		.attr("width", r*3)
		.attr("height", r*3 - 150)
		.append("svg:g")
		.attr("transform", "translate(" + (r+offset )+ "," + (r+offset ) + ")");
    
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
		.attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")" })
		// .attr("transform", function(d) { return "scale(" + 1 + ")translate(" + 1 + ")" });
        
        
        
		node.append("svg:circle")
		.attr("r", function(d) {
            if(d.children)
                return d.children.length/5 + 4; 
            return  4; })
        .on("mouseover", function(d, i) { 
                d3.select("#monitor").html(d.name) 
                var t = d3.select(d3.event.currentTarget.parentNode).selectAll("text");
                t.attr("visibility", "visible");
                
            })
         .on("mouseout", function(d) {
          var t = d3.select(d3.event.currentTarget.parentNode).selectAll("text");
                t.attr("visibility", "hidden")
            })   
         .on("click", function(d, i) {
                if(isBranch(d)) {
                    if(d.hParent) {
                        hTree(d.hParent, target);
                        d.hParent = null;
                    } else {
                        d.hParent = root;
                        hTree(d, target) 
                    }
                }
            });
    
    
    
    node.append("svg:text")
		.attr("dx", function(d) { return d.x < 180 ? 20 : -20; })
		.attr("dy", ".31em")
		.attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
		.attr("transform", function(d) { return d.x < 180 ? null : "rotate(180)"; })
		.text(function(d) { return d.name; }).attr("visibility", "hidden");
    
    
    //	});
}
