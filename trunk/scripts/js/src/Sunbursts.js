function sunburst (rawJson) {
	/*
	d3.json("../data/flare.json", function(json) {
	*/
	
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
		function sunburstRoots(data) {
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
					that.children = that.children.sort(sortNodes);
				}


				var callback = function(ev) {
					d3.select("#monitor").html(that.name) 
					_sunburst(that.children, target,that.name) 
				};

				el.append("li").html("a").html(that.name).on("click", 
				callback
				);

			});
			d3.select("#monitor").html(json[0].name)
			_sunburst(json[0].children,target,json[0].name);

		}
	
	  sunburstRoots(rawJson)

	}
	function _sunburst(root, target,visModel,augmentationCallback) { 
		var w = canvas_height-5,
		h = w,
		r = w / 2,
		x = d3.scale.linear().range([0, 2 * Math.PI]),
		y = d3.scale.pow().exponent(1.3).domain([0, 1]).range([0, r]),
		p = 0,
		duration = 1000;
		var color = d3.scale.category20c();
		var div = d3.select(target);
		div.html("");
		var vis = div.append("svg:svg")
		.attr("width", canvas_width + p * 2)
		.attr("height", canvas_height + p * 2)
		.append("svg:g")
		.attr("transform", "translate(" + (canvas_width/2.0) + "," + (canvas_height/2.0) + ")");

		var partition = d3.layout.partition()
		.sort(null)
		.value(function(d) { 
			return 15.8 - d.depth; 
		});

		var arc = d3.svg.arc()
		.startAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x))); })
		.endAngle(function(d) { return Math.max(0, Math.min(2 * Math.PI, x(d.x + d.dx))); })
		.innerRadius(function(d) { return Math.max(0, d.y ? y(d.y) : d.y); })
		.outerRadius(function(d) { return Math.max(0, y(d.y + d.dy)); });

		var json = root;
		var nodes = partition.nodes({children: json});

		if (vis.name)
			nodes[0].name = vis.name();
		else
			nodes[0].name = "Sunburst Root";
		nodes[0].properties = {
			name: nodes[0].name;
		}
		nodes[0].isRoot = true;

		var path = vis.selectAll("path").data(nodes);
		path.enter().append("svg:path")
		.attr("id", function(d, i) { 
			return "path-" + i; 
		})
		.attr("d", arc)
		.attr("fill-rule", "evenodd")
		.style("fill", colour)
		.on("click", click)
        /*.on("mouseover", function(d, i) { 
                d3.select("#monitor").html(d.name) 
                var t = d3.select(d3.event.currentTarget);
				var id = t.attr("id");
				var pathIdTag = "path-"
				if (id.substring(0,pathIdTag.length) == pathIdTag) {
					nId = id.substring(pathIdTag.length);
					t = d3.select("#text-" + nId)
				}
				var elements = t.selectAll("tspan");
                elements.attr("visibility", "visible");

            })
         .on("mouseout", function(d) {
				var t = d3.select(d3.event.currentTarget);
				var id = t.attr("id");
				var pathIdTag = "path-"
				if (id.substring(0,pathIdTag.length) == pathIdTag) {
					nId = id.substring(pathIdTag.length);
					t = d3.select("#text-" + nId)
				}
				var elements = t.selectAll("tspan")
                elements.attr("visibility", "hidden");
            })*/.call(visModel.augment("nodes",augmentationCallback));

		/*var text = vis.selectAll("text").data(nodes);
		var textEnter = text.enter().append("svg:text")
		.style("opacity", 1)
		.attr("id", function(d, i) {
			 return "text-" + i; })
		.style("fill", function(d) {
			return brightness(d3.rgb(colour(d))) < 125 ? "#eee" : "#000";
		})
		.attr("text-anchor", function(d) {
			return x(d.x + d.dx / 2) > Math.PI ? "end" : "start";
		})
		.attr("dy", ".2em")
		.attr("transform", function(d) {
			if (d.isRoot)
			return "";
			var multiline = (d.name || "").split(" ").length > 1,
			angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
			rotate = angle + (multiline ? -.5 : 0);
			return "rotate(" + rotate + ")translate(" + (y(d.y) + p) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
		})
		.on("click", click)
		.on("mouseover", function(d, i) { 
                d3.select("#monitor").html(d.name) 
                var t = d3.select(d3.event.currentTarget);
				var elements = t.selectAll("tspan");
                elements.attr("visibility", "visible");

            })
         .on("mouseout", function(d) {
				var t = d3.select(d3.event.currentTarget);
				var elements = t.selectAll("tspan")
                elements.attr("visibility", "hidden");
            });
		textEnter.append("svg:tspan")
		.attr("x", 0)
		.attr("visibility","hidden")
		.text(function(d) { 
			if (d.isRoot)
				return d.name;
			return d.depth ? d.name.split(" ")[0] : ""; });
		textEnter.append("svg:tspan")
		.attr("x", 0)
		.attr("dy", "1em")
		.attr("visibility","hidden")
		.text(function(d) { return d.depth ? d.name.split(" ")[1] || "" : ""; });*/

		function click(d) {
			path.transition()
			.duration(duration)
			.attrTween("d", arcTween(d));

			/*text
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
				if (d.isRoot)
					return "";
				var multiline = (d.name || "").split(" ").length > 1;
				return function() {
					var angle = x(d.x + d.dx / 2) * 180 / Math.PI - 90,
					rotate = angle + (multiline ? -.5 : 0);
					return "rotate(" + rotate + ")translate(" + (y(d.y) + p) + ")rotate(" + (angle > 90 ? -180 : 0) + ")";
				};
			})
			//.style("opacity", function(e) { return isParentOf(d, e) ? 1 : 1e-6; })
			.each("end", function(e) {
				d3.select(this).style("visibility", isParentOf(d, e) ? null : "hidden");
			});*/
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