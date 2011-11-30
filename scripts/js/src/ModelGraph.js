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

function sunburst () {
    d3.select("#chart").html("");
	var w = 840,
	h = w,
	r = w / 2,
	x = d3.scale.linear().range([0, 2 * Math.PI]),
	y = d3.scale.pow().exponent(1.3).domain([0, 1]).range([0, r]),
	p = 5,
	duration = 1000;
	var color = d3.scale.category20c();
	var div = d3.select("#chart");

	var vis = div.append("svg:svg")
	.attr("width", w + p * 2)
	.attr("height", h + p * 2)
	.append("svg:g")
	.attr("transform", "translate(" + (r + p) + "," + (r + p) + ")");

	var partition = d3.layout.partition()
	.sort(null)
	.value(function(d) { return 5.8 - d.depth; });

	var arc = d3.svg.arc()
	.startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
	.endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
	.innerRadius(function(d) { return Math.max(0, d.y ? y(d.y) : d.y); })
	.outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });

	var json = dataArray();
	var nodes = partition.nodes({children: json});

	var path = vis.selectAll("path").data(nodes);
	path.enter().append("svg:path")
	.attr("id", function(d, i) { return "path-" + i; })
	.attr("d", arc)
	.attr("fill-rule", "evenodd")
	.style("fill", colour)
	.on("click", click);

	var text = vis.selectAll("text").data(nodes);
	var textEnter = text.enter().append("svg:text")
	.style("opacity", 1)
	.style("fill", function(d) {
		return brightness(d3.rgb(colour(d))) < 125 ? "#eee" : "#000";
	})
	.attr("text-anchor", function(d) {
		return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
	})
	.attr("dy", ".2em")
	.attr("transform", function(d) {
		var multiline = (d.name || "").split(" ").length > 1,
		angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
		rotate = angle + (multiline ? -.5 : 0);
		return "rotate(" + rotate + ")translate(" + (y(d.y) + p) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
	})
	.on("click", click);
	textEnter.append("svg:tspan")
	.attr("x", 0)
	.text(function(d) { return d.depth ? d.name.split(" ")[0] : ""; });
	textEnter.append("svg:tspan")
	.attr("x", 0)
	.attr("dy", "1em")
	.text(function(d) { return d.depth ? d.name.split(" ")[1] || "" : ""; });

	function click(d) {
		path.transition()
		.duration(duration)
		.attrTween("d", arcTween(d));

		// Somewhat of a hack as we rely on arcTween updating the scales.
		text
		.style("visibility", function(e) {
			return isParentOf(d, e) ? null : d3.select(this).style("visibility");
		})
		.transition().duration(duration)
		.attrTween("text-anchor", function(d) {
			return function() {
				return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
			};
		})
		.attrTween("transform", function(d) {
			var multiline = (d.name || "").split(" ").length > 1;
			return function() {
				var angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
				rotate = angle + (multiline ? -.5 : 0);
				return "rotate(" + rotate + ")translate(" + (y(d.y) + p) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
			};
		})
		.style("opacity", function(e) { return isParentOf(d, e) ? 1 : 1e-6; })
		.each("end", function(e) {
			d3.select(this).style("visibility", isParentOf(d, e) ? null : "hidden");
		});
	}

	function isParentOf(p, c) {
		if (p === c) return true;
		if (p.children) {
			return p.children.some(function(d) {
				return isParentOf(d, c);
			});
		}
		return false;
	}

	function colour(d) {
		/*if (d.children) {
			// There is a maximum of two children!
			var colours = d.children.map(colour),
			a = d3.hsl(colours[0]),
			b = d3.hsl(colours[1]);
			// L*a*b* might be better here...
			return d3.hsl((a.h + b.h) / 2, a.s * 1.2, a.l / 1.2);
		}*/
		if (d.children)
			return color((d.children ? d : d.parent).name);
		var col = color(d.parent.name);
		var rand = Math.random()+0.1;
		var r = hexToR(col);
		var g = hexToG(col);
		var b = hexToB(col);
		return d3.rgb(r*rand,g*rand,b*rand);
	}

	function hexToR(h) {return parseInt((cutHex(h)).substring(0,2),16)}
	function hexToG(h) {return parseInt((cutHex(h)).substring(2,4),16)}
	function hexToB(h) {return parseInt((cutHex(h)).substring(4,6),16)}
	function cutHex(h) {return (h.charAt(0)=="#") ? h.substring(1,7):h}

	// Interpolate the scales!
	function arcTween(d) {
		var my = maxY(d),
		xd = d3.interpolate(x.domain(), [d.x, d.x + d.dx]),
		yd = d3.interpolate(y.domain(), [d.y, my]),
		yr = d3.interpolate(y.range(), [d.y ? 20 : 0, r]);
		return function(d) {
			return function(t) { x.domain(xd(t)); y.domain(yd(t)).range(yr(t)); return arc(d); };
		};
	}

	function maxY(d) {
		return d.children ? Math.max.apply(Math, d.children.map(maxY)) : d.y + d.dy;
	}

	// http://www.w3.org/WAI/ER/WD-AERT/#color-contrast
	function brightness(rgb) {
		return rgb.r * .299 + rgb.g * .587 + rgb.b * .114;
	}
}


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
