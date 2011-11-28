function tree() {
d3.select("#chart").html("");
    var json = //{
      //  "name": "",
      //  "children": [
            {
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
           // }
           // ]
    };
    
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
        .attr("transform", "translate(" + (r + 50)+ "," + (r + 50) + ")");
        
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
        .attr("r", 10);
        
    
    node.append("svg:text")
    .attr("dx", function(d) { return d.x < 180 ? 40 : -40; })
    .attr("dy", ".31em")
    .attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
    .attr("transform", function(d) { return d.x < 180 ? null : "rotate(180)"; })
    .text(function(d) { return d.name; });
}

function graph() {
d3.select("#chart").html("");
}