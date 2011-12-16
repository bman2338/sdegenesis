function plotSeries(series, xSeriesName, ySeriesName){
	var maxY = -1;
	var minY = 1;
	var minX = 1;
	var maxX = 1;
	var circlesValues = [];
	
	
	for(var i = 0; i < series.length; ++i){
				
		for(var j = 0; j < series[i].serie.length; ++j){
			if(series[i].serie[j].y > maxY){
				maxY = series[i].serie[j].y;
			}
			if(series[i].serie[j].y < minY){
				minY = series[i].serie[j].y;
			}
			if(series[i].serie[j].x > maxX){
				maxX = series[i].serie[j].x;
			}
			if(series[i].serie[j].x < minX){
				minX = series[i].serie[j].x;
			}
			circlesValues.push(series[i].serie[j]);
		}
	}
	
	var w = 5000;
	var h = 512;
	var padding = 100;
	var labelPadding = 50;
	var legendPadding = 50;
	var xscaleTicks = 10;
	var yscaleTicks = 10;
	
	var x = d3.scale.linear().domain([Math.floor(minX), Math.ceil(maxX)]).range([0, w]);
	var y = d3.scale.linear().domain([Math.floor(minY), Math.ceil(maxY)]).range([h, 0]);

	var vis = d3.select("body")
	.append("svg:svg")
	.attr("width", w + padding * 2)
	.attr("height", h + padding * 2)
	.append("svg:g")
	.attr("transform", "translate(" + padding + "," + padding + ")");


	var xRules = vis.selectAll("g.rule")
	.data(x.ticks(xscaleTicks))
	.enter().append("svg:g")
	.attr("fill","#000");
	
	var yRules = vis.selectAll("g.rule")
	.data(y.ticks(yscaleTicks))
	.enter().append("svg:g")
	.attr("fill","#000");

	//Horizontal Grid Lines
	xRules.append("svg:line")
	.attr("x1", x)
	.attr("x2", x)
	.attr("y1", 0)
	.attr("y2", h - 1)
	.attr("stroke", "#eee")
	.attr("shape-rendering","crispEdges");

	//Vertical Grid Lines
	yRules.append("svg:line")
	.attr("y1", y)
	.attr("y2", y)
	.attr("x1", 0)
	.attr("x2", w + 1)
	.attr("stroke", "#eee")
	.attr("shape-rendering","crispEdges");

	// X axis scale
	xRules.append("svg:text")
	.attr("x", x)
	.attr("y", h + 10)
	.attr("dy", ".71em")
	.attr("text-anchor", "middle")
	//.text(x.tickFormat(xscaleTicks));
	.text(function(d,i){return getSerieLabel(series,d,"x")});


	// Y axis scale
	yRules.append("svg:text")
	.attr("y", y)
	.attr("x", -3)
	.attr("dy", ".35em")
	.attr("text-anchor", "end")
	.text(y.tickFormat(yscaleTicks));
	
	//Axis Labels
	vis.append("svg:text")
	.attr("x", w/2)
	.attr("y", h+labelPadding)
	.attr("dy", ".71em")
	.attr("text-anchor", "middle")
	.attr("font-weight", "bold")
	.text(xSeriesName);
	
	vis.append("svg:text")
	.attr("x", -h/2)
	.attr("y", -labelPadding)
	.attr("dy", ".71em")
	.attr("text-anchor", "middle")
	.attr("font-weight", "bold")
	.attr("transform","rotate(270, 0, 0)")
	.text(ySeriesName);

	var line = d3.svg.line()
	.x(function(d) { return x(d.x); })
	.y(function(d) { return  y(d.y); });

	
	//Generate Lines
	for(var i = 0; i < series.length; ++i){
		vis.append("svg:path")
		.attr("d", line(series[i].serie))
		.attr("fill", "none")
		.attr("stroke", series[i]["lineColor"])
		.attr("stroke-width", series[i]["lineSize"]);	
	}
	

	//Circle points
	var circles = vis.selectAll("circle.line")
	.data(circlesValues)
	.enter().append("svg:circle")
	.attr("cx", function(d) { return x(d.x); })
	.attr("cy", function(d) { return y(d.y); })
	.attr("r", 2.5)
	.attr("stroke", "black")
	.attr("fill", function(d,i) {return getSerieProperty(series,i, "nodeColor")} )
	.on("mouseover", function(d,i) { tooltip.show(getSerieProperty(series,i,"serieName") + ":\t"+d.y+" files."+"</br></br>Date: " + d.label); return d3.select(this).attr("r",5)})
	.on("mouseout", function(d,i) { tooltip.hide(); return d3.select(this).attr("r",2.5)});

	

	d3.select(window).on("keydown", function() {   
				var displacement = 0;  
				switch (d3.event.keyCode) {
					case 37: displacement = -10; break;
					case 39: displacement = 10; break;
					default:
						return;
				}
				
				repaint(displacement,300);
			});
			
			function repaint (displacement,minStep) {
				// if(displacement < 0 && minDomain <= 0)
				// 		return;
				// 	if(displacement > 0 && maxDomain >= maxX)
				// 		return;
				
			
				
				var vals = [{x:21,y:2},{x:22,y:2},{x:23,y:2},{x:24,y:2}];
			
				
				circles.data(vals, function(d) { return d; })
					.enter().append("svg:circle")
					.attr("cx", function(d) { return x(d.x); })
					.attr("cy", function(d) { return y(d.y); })
					.attr("r", 3.5)
					.attr("stroke", "black")
					.attr("fill", function(d,i) {return getSerieProperty(series,i, "nodeColor")} )
					.on("mouseover", function(d,i) { tooltip.show(getSerieProperty(series,i,"serieName") + ":\t"+d.y+" files."+"</br></br>Date: " + d.label); return d3.select(this).attr("r",8)})
					.on("mouseout", function(d,i) { tooltip.hide(); return d3.select(this).attr("r",3.5)})
					.transition()
						.duration(750).attr("transform", function(d) { return "translate(" + x(displacement) + ",0)"; })
				
				
				circles.transition()
					.duration(750)
					.attr("transform", function(d) { return "translate(" + x(displacement) + ",0)"; });
				
				circles.exit().transition();
			
				 var lines = vis.selectAll("path");
				
				vis.append("svg:path")
				.attr("d", line(vals))
				.attr("fill", "none")
				.attr("stroke", "#ff0000")
				.attr("stroke-width", "#ff000"); 	
				
				lines.transition()
					.duration(750)
					.attr("transform", function(d) { return "translate(" + x(displacement) + ",0)"; });
					
				lines.remove();
				
				xRules.transition()
					.duration(750)
					.attr("transform", function(d) { return "translate(" + x(displacement) + ",0)"; });
				xRules.remove();
				yRules.remove();
				
			
			}
}







function getSerieProperty(series,globalIndex,propertyName){
	
	var relativeIndex = globalIndex;
	for(var i = 0; i < series.length; ++i){
    
        if(relativeIndex >= series[i]["serie"].length){
            relativeIndex -= series[i]["serie"].length;
			continue;
        }
	
        if(series[i][propertyName]){
            return series[i][propertyName];
        }
        else{
            return d3.rgb(0,0,0);
        }
    }
	
}

function getSerieLabel(series, index, serieName){
	for(var i = 0; i < series.length; ++i){
		for(var j = 0; j < series[i]["serie"].length; ++j){
			if(series[i]["serie"][j][serieName] == index ){
				if(series[i]["serie"][j].label){
					return series[i]["serie"][j].label;
				}	
				return index;
			}
		}
	}
}

function getCommitHistoryData(	history){
	var result = {};
	var revCounter = 0;
	var commitHistory = [];
	var format = d3.time.format(getHistoryDateFormatString());
	var vizFormat = d3.time.format("%d/%m/%Y");
	for(var rev in history){
		if(history[rev]){
			var parsedDate = format.parse(getDateStrNoZone(history[rev].date));
			var dateStr = vizFormat(parsedDate);
			if(!result[history[rev].author]){
				result[history[rev].author]=[];
				result[history[rev].author].push({ x: revCounter, y: 1, label: dateStr });
				
				++revCounter;
				continue;
			}
			var last = result[history[rev].author].length + 1;
			result[history[rev].author].push({ x: revCounter, y: last, label: dateStr });
			++revCounter;
		}
	}
	
	//var color = d3.interpolateRgb("#ff0000", "#0000ff","#002176","#128392","#ff4422","#335577","#123243","#778899","#210021");
	var color = ["#ff0000", "#0000ff","#002176","#128392","#ff4422","#335577","#123243","#778499","#210021","#550021","#556621"];
	var i = 0;
	for(var author in result){
		var obj = {};
		obj.serie = result[author];
		obj["lineSize"] = 2;
		obj["lineColor"] = color[i];
		obj["nodeColor"] = color[i];
		obj.serieName = author;
		commitHistory.push(obj);
		++i;
	}
	
	return commitHistory;
}


function getChangesHistory(history){
	var series = [[],[],[],[]];
	var seriesNames = ["Added Files", "Modified Files", "Deleted Files", "General"];
	var seriesColors = [d3.rgb(0,189,0),d3.rgb(0,0,240),d3.rgb(224,0,0), d3.rgb(75,75,75)];
	var format = d3.time.format(getHistoryDateFormatString());
	var vizFormat = d3.time.format("%d/%m/%Y");
	var revCount = 0;
	for(var rev in history){
		if(history[rev]){
			var parsedDate = format.parse(getDateStrNoZone(history[rev].date));
			var dateStr = vizFormat(parsedDate);
			var total = history[rev].addedFilesCount + history[rev].modifiedFilesCount + history[rev].deletedFilesCount;
			series[0].push({x: revCount, y: history[rev].addedFilesCount, label : dateStr});
			series[1].push({x: revCount, y: history[rev].modifiedFilesCount, label : dateStr});
			series[2].push({x: revCount, y: history[rev].deletedFilesCount, label : dateStr});	
			series[3].push({x: revCount, y: total, label : dateStr});	
			++revCount;
		}
	}
	
	
	var result = [];	
	for(var i = 0; i < series.length; ++i){
		var obj = {};
		obj.serie = series[i];
		obj.serieName = seriesNames[i];
		obj["lineSize"] = 2;
		obj["lineColor"] = seriesColors[i];
		obj["nodeColor"] = seriesColors[i];
		result.push(obj);
	}
	
	return result;
}


