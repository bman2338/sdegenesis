function roots() {
var target = "#chart";
var rootsTarget = "#roots";

d3.select(rootsTarget).html("");

var json = data4().children;
var el = d3.select(rootsTarget);

json = json.sort(function(a, b) {
if(b.children) {
    if(a.children) {
        return b.children.length - a.children.length;
        } else { return 1 };
    }
    else {
        return -1;
    }
});

el = el.append("list");
jQuery.each(json, function() {
    var that = this;
    var callback = function(ev) { 
        tree(that, target) 
    };
    
    el.append("li").html("a").html(that.name).on("click", 
    callback
    );//.on("mouseover", callback);

});
tree(json[0], target);

}

function tree(root, target) {
    d3.select(target).html("");
    var json = root;
//	d3.json("data3.json", 
	
//	function(json) {
    
		var r = 720/2;
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
		.attr("transform", "translate(" + (r )+ "," + (r ) + ")");

		var nodes = tree.nodes(json);
		var link = vis.selectAll("path.link")
		.data(tree.links(nodes))
		.enter().append("svg:path")
		.attr("class", "link")
		.attr("d", diagonal);

		var node = vis.selectAll("g.node")
		.data(nodes)
		.enter().append("svg:g")
		.attr("class", "node")
		.attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")" })
		// .attr("transform", function(d) { return "scale(" + 1 + ")translate(" + 1 + ")" });

		node.append("svg:circle")
		.attr("r", 2.3);


		node.append("svg:text")
		.attr("dx", function(d) { return d.x < 180 ? 10 : -10; })
		.attr("dy", ".31em")
		.attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
		.attr("transform", function(d) { return d.x < 180 ? null : "rotate(180)"; })
		.text(function(d) { return d.name; });
//	});
}
