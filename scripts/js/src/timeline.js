var n = 3, // number of layers
    m, // number of revisions
	data,				//added , modified, deleted
	colorsMse = new Array("#404ACF","#50CC3D","#CC5241");
	colorsNo = new Array("#8FF", "#888", "#F88");
	colorsHi = new Array("#A9A9A9","#EEE", "#CCC");
	modType = new Array("addedFilesCount","modifiedFilesCount","deletedFilesCount");
	currentLast = 0,
	currentFirst = 0,
	currentScale = 0,
	currenX = 0,
	currenY = 0,
	bars = null;
var revisionsMap =[];
var cleanedHist;
var layers;
    
var p = 20,
    w, //size x
    h, //size y
    mx,my,mz,
    x = function(d) { return d.x * w / mx; }, // position of d
    y0 = function(d) { return h - d.y0 * h / my; }, //height of 1st bar
    y1 = function(d) { return h - (d.y + d.y0) * h / my; }, //height of 2nd bar
    y2 = function(d) { return d.y * h / mz; }; // or `my` to not rescale, height of 3rd bar
   
function displayTimeline(hist, sizeX, sizeY, scale){
	stackedBarChart(hist,sizeX,sizeY,scale,hist.last,300)
}

function getRevisions (history,to,step) {
	var revs = {}
	var i = to;
	for (i; i > to - step && i >= 0; --i) {
		var rev = cleanedHist[i];
		revs[i] = rev;
	};
	return {
		first: i,
		revs: revs,
	};
}

function stackedBarChart (data,sizeX,sizeY,scale,version,step) {

    d3.select("#chart").html("");
	$("#chart").html = "";
	currentLast = version;
	currentX = sizeX;
	currentY = sizeY;
	w = sizeX;
	h = sizeY - .5 - p;
	cleanedHist = data;
	currentScale = scale;
	
	var revsData = getRevisions(cleanedHist,version,step);
	var revs = revsData.revs;
		
	currentFirst = revsData.first;
	
	data = getData(revs, scale);
	m= data[0].length;
	d3.layout.stack()(data);
	
	for (var i = 0; i < data.length; ++i) {
		for (var j = 0; j < data[i].length; ++j) {
			data[i][j].columnIndex = i;
		}
	}
	
	mx = m;
	
	my = d3.max(data, function(d) {
      return d3.max(d, function(d) {
       return d.y0 + d.y;
      });
    });

    //my=300;
    mz = d3.max(data, function(d) {
      return d3.max(d, function(d) {
        return d.y;
      });
    });

	
	//svg containing the whole graph
	var vis = d3.select("#chart")
		.append("svg:svg")
		.attr("width", w)
		.attr("height", h + p);
	// set color of the bars
	
	
	vis.append("svg:text")
	   .attr("x", w-50)
	   .attr("y", h+10)
	   .attr("dy", ".71em")
	   .attr("text-anchor", "middle")
	   .text("Rev."+currentLast);

	vis.append("svg:text")
	   .attr("x", 50)
	   .attr("y", h+10)
	   .attr("dy", ".71em")
	   .attr("text-anchor", "middle")
	   .text("Rev."+currentFirst);
	
	layers = vis.selectAll("g.layer")
		.data(data)
		.enter().append("svg:g")
		.style("fill", function(d, i) { return colorsMse[i]; })
		.attr("class", "layer");



	//add bars 
	bars = layers.selectAll("g.bar")
		.data(function(d) { return d; })
		.enter().append("svg:g")
		.attr("class", "bar")
		.attr("transform", function(d) { return "translate(" + x(d) + ",0)"; })
		.on("click", function(d, i) {
			highlightCurrent(i);
			/*projects[0][currentProject].getRevision(projects[0][currentProject], cleanedHist.i, function(rev){
				projects[0][currentProject].currRev = rev.revisionNumber;
			});*/
	});
	
	
 	//append rectangles to the bar	
	bars.append("svg:rect").on("mouseover", function(d, i) { 
			var revisionNumber=revisionsMap[i];
			var currentHistory = cleanedHist[revisionNumber];
			if (!currentHistory)
				return;
			var modtype = modType[d.columnIndex];
			tooltip.show(modtype + ": "+currentHistory[modtype] +"<br>Revision: " + revisionNumber + "<br>Author: " + currentHistory.author +"<br>Date: " + currentHistory.date+"");
		})
		.on("mouseout", function(d, i){
			tooltip.hide();
		})
		.attr("width", x({x: .9}))
		.attr("x", 0)
		.attr("y", h)
		.attr("height", 0)
		.transition()
		.delay(function(d, i) { return i * 10; })
		.attr("y", y1)
		.attr("height", function(d) { return y0(d) - y1(d); });


	vis.append("svg:line")
		.attr("x1", 0)
		.attr("x2", w - x({x: .1}))
		.attr("y1", h)
		.attr("y2", h);
		
		function highlightCurrent(selected){
		 	var l = layers.selectAll("g.layer rect")
		 	l.style("stroke", function(d, i){
				if(selected == i) {
					return "#f00";
				}
		}).style("stroke-width", function(d, i){
				if(selected == i) {
					return "2px";
				}
		});
		

}



d3.select(window).on("keydown", function() {   
	var displacement = 0;  
	switch (d3.event.keyCode) {
		case 37: displacement = -200; break;
		case 39: displacement = 200; break;
		default:
			return;
	}
	repaint(displacement,300);
});

}

function repaint (displacement,minStep) {
	var ver = currentFirst+displacement;
	if (displacement > 0 && ver == currentLast)
		return;
	else if (displacement < 0 && ver == currentFirst)
		return;
		
	var last;
		
	if (ver < 0) {
		ver = 0;
		last = minStep;
	}
	else if (ver > cleanedHist.last) {
		ver = cleanedHist.last;
		last = ver;
	}
	else {
		last = ver + minStep;
		if (last > cleanedHist.last)
			last = cleanedHist.last;
	}
	
	/*currentLast = last;
	currentFirst = last - minStep;
	
	var revsData = getRevisions(cleanedHist,last,minStep);
	data = getData(revsData.revs,currentScale);
		
	layers.transition().duration(750).attr("transform",function(d) {
		return "translate(" + (last-currentLast) + ",0)";
	});		
	bars.selectAll("rect").data(function (d) {
			return d;
	}).transition().duration(750).attr("height",y);*/
	stackedBarChart(cleanedHist,currentX,currentY,currentScale,last,minStep);
}


/*
 * format the history
 */
function getData(history, scale){
	var result = [[],[],[]];
	var i=0;
	
	for(var rev in history){
		if (!history[rev]) {
			result[0].push({x:0,y:0});
			result[1].push({x:0,y:0});
			result[2].push({x:0,y:0});
			++i;
		}
		else {
			result[0].push({x: i, y: scale(history[rev].addedFilesCount)});
			result[1].push({x: i, y: scale(history[rev].modifiedFilesCount)});
			result[2].push({x: i, y: scale(history[rev].deletedFilesCount)});	
			revisionsMap[i] = rev;
			++i;
		}
	}	
	return result;
}


function highlightMse(){
	var group = d3.selectAll("#chart");
	
	group.selectAll("g.layer rect")
		.transition()
			.duration(500)
			.style("fill", function(d, i){
			var revisionNumber=revisionsMap[d.x];
			var currentHistory = cleanedHist[revisionNumber]; 
			if(currentHistory && currentHistory.hasMse)
				return colorsMse[d.columnIndex];
			return colorsHi[d.columnIndex];	
		});
		
	group.select("#highlight")
		.attr("onclick", "undoHighlight()");
}

function undoHighlight(){
	var group = d3.selectAll("#chart");
	
	group.selectAll("g.layer rect")
		.transition()
			.duration(500)
			.style("fill", function(d, i){
			var revisionNumber=revisionsMap[d.x];
			var currentHistory = cleanedHist[revisionNumber];
			if(currentHistory.hasMse)
				return colorsMse[d.columnIndex];
			return colorsMse[d.columnIndex];	
		});
		
	group.select("#highlight")
		.attr("onclick", "highlightMse()");
}
 

function transitionGroup() {
  var group = d3.selectAll("#chart");

  group.select("#group")
      .attr("class", "first active");

  group.select("#stack")
      .attr("class", "last");

  group.selectAll("g.layer rect")
    .transition()
      .duration(500)
      .delay(function(d, i) { return (i % m) * 10; })
      .attr("x", function(d, i) { return x({x: 1 * ~~(i / m) / n}); })
      .attr("width", x({x: .9 / n}))
      .each("end", transitionEnd);

  function transitionEnd() {
    d3.select(this)
      .transition()
        .duration(500)
        .attr("y", function(d) { return h - y2(d); })
        .attr("height", y2);
  }
}


function transitionStack() {
  var stack = d3.select("#chart");

  stack.select("#group")
      .attr("class", "first");

  stack.select("#stack")
      .attr("class", "last active");

  stack.selectAll("g.layer rect")
    .transition()
      .duration(500)
      .delay(function(d, i) { return (i % m) * 10; })
      .attr("y", y1)
      .attr("height", function(d) { return y0(d) - y1(d); })
      .each("end", transitionEnd);

  function transitionEnd() {
    d3.select(this)
      .transition()
        .duration(500)
        .attr("x", 0)
        .attr("width", x({x: .9}));
  }
}




