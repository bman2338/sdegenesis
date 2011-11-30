/*function data() {
    
        var json=   {
            "name": "Object",
            "children": [
                {
                    "name": "ModelObject",
                    "children": [
                        {"name": "FamixObject", "size": 3938},
                        {"name": "ValueObject", "size": 3812},
                        
                        ]
                },
                {
                    "name": "Navigator",
                    "children": [
                        {"name": "BreadthFirstNavigator", "size": 3534},
                        {"name": "DepthFirstNavigator", "size": 5731}
                        ]
                }
                ]
    };
        
        return json;
        
}
*/

function methods() {
d3.select("#methods").html("");
var json = data().children;



}


function tree() {
    d3.select("#chart").html("");
    var json = data();
//	d3.json("data3.json", 
	
//	function(json) {
    
		var r = 960/2;
		var tree = d3.layout.tree()
		.size([360, r - 120])
		.separation(function(a,b) { return (a.parent == b.parent ? 1: 2)/a.depth });

		var diagonal = d3.svg.diagonal.radial()
		.projection(function(d) { return [ d.y, d.x /180 * Math.PI]; });



		var vis = d3.select("#chart")
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



function graph() {
    //TODO
    d3.select("#chart").html("");
}




function hist() {
d3.select("#chart").html("");
    var n = 10000, // number of trials
    m = 10,    // number of random variables
    data = [];
    
    // Generate an Irwin-Hall distribution.
    for (var i = 0; i < n; i++) {
        for (var s = 0, j = 0; j < m; j++) {
            s += Math.random();
        }
        data.push(s);
    }
    
    var w = 400,
        h = 400;
    
    var histogram = d3.layout.histogram()
        (data);
    
    var x = d3.scale.ordinal()
        .domain(histogram.map(function(d) { return d.x; }))
        .rangeRoundBands([0, w]);
    
    var y = d3.scale.linear()
        .domain([0, d3.max(histogram, function(d) { return d.y; })])
        .range([0, h]);
    
    var vis = d3.select("#chart").append("svg:svg")
        .attr("width", w)
        .attr("height", h)
        .append("svg:g")
        .attr("transform", "translate(.5)");
    
    vis.selectAll("rect")
        .data(histogram)
        .enter().append("svg:rect")
        .attr("transform", function(d) { return "translate(" + x(d.x) + "," + (h - y(d.y)) + ")"; })
        .attr("width", x.rangeBand())
        .attr("y", function(d) { return y(d.y); })
        .attr("height", 0)
        .transition()
        .duration(750)
        .attr("y", 0)
        .attr("height", function(d) { return y(d.y); });
    
    vis.append("svg:line")
        .attr("x1", 0)
        .attr("x2", w)
        .attr("y1", h)
        .attr("y2", h);
    
}


function stacked() {
//TODO http://mbostock.github.com/d3/ex/stack.html
}

function revisions() {
//TODO http://mbostock.github.com/d3/ex/calendar.html
//activities LOC, developer actives
//
}
