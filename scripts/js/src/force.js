
function forceDirectedGraph(nodes, edges,visModel,augmentationCallback) {
	var w = canvas_width,
	h = canvas_height,
	fill = d3.scale.category20();
	d3.select("#chart").html("");
	var vis = d3.select("#chart").append("svg:svg")
	.attr("width", w)
	.attr("height", h);
	//.attr("z-index", 100);


	var force = d3.layout.force()
	.charge(-100)
	.linkDistance(300)
	.nodes(nodes)
	.links(edges)
	.size([w, h])
	.start();

	var link = vis.selectAll("line.link")
	.data(edges)
	.enter().append("svg:line")
	.attr("class", "link")
	.style("stroke-width", function(d) { return Math.sqrt(d.value); })
	.attr("x1", function(d) { return d.source.x; })
	.attr("y1", function(d) { return d.source.y; })
	.attr("x2", function(d) { return d.target.x; })
	.attr("y2", function(d) { return d.target.y; });

	var node = vis.selectAll("circle.node")
	.data(nodes)
	.enter().append("svg:circle")
	.attr("class", "node")
	.attr("cx", function(d) { return d.x; })
	.attr("cy", function(d) { return d.y; })
	.attr("r", 10)
	//.style("fill", function(d) { return fill(d.group); })
	.call(force.drag);

	if (augmentationCallback) {
		node.call(visModel.augment("nodes",augmentationCallback));
	}

	force.on("tick", function() {
		link.attr("x1", function(d) { return d.source.x; })
		.attr("y1", function(d) { return d.source.y; })
		.attr("x2", function(d) { return d.target.x; })
		.attr("y2", function(d) { return d.target.y; });

		node.attr("cx", function(d) { return d.x; })
		.attr("cy", function(d) { return d.y; });
	});
}
