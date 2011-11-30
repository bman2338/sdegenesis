function sortNodes(a, b) {
if(b.children) {
    if(a.children) {
        return b.children.length - a.children.length;
        } else  return 1;
    }
    else return -1;
}


function roots() {
var target = "#chart";
var rootsTarget = "#roots";

d3.select(rootsTarget).html("");

var json = data4().children;
var el = d3.select(rootsTarget);

json = json.sort(sortNodes);


el = el.append("list");
jQuery.each(json, function() {
    var that = this;
    if(that.children) {
        that.children = that.children.sort(function(a, b) { 
        var res = sortNodes(a,b); 
        if(res == 0) {
        res = a.name > b.name;
        }
        return res; });
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
    
    var offset = 0;
		var r = 1024/2;
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
		.attr("class", function(d) { var clazz = "node"; if(isBranch(d)) { return " Branch"; } return clazz;})
		.attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")" })
		// .attr("transform", function(d) { return "scale(" + 1 + ")translate(" + 1 + ")" });


 
		node.append("svg:circle")
		.attr("r", function(d) 
         {
           if(d.children)
                return d.children.length/5 + 4; 
            return  3; }).on("mouseover", function(d, i) { 
                d3.select("#monitor").html(d.name) 
                
            }).on("click", function(d, i) {
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
		.attr("dx", function(d) { return d.x < 180 ? 10 : -10; })
		.attr("dy", ".31em")
		.attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
		.attr("transform", function(d) { return d.x < 180 ? null : "rotate(180)"; })
		.text(function(d) { return d.name; });
        

//	});
}
