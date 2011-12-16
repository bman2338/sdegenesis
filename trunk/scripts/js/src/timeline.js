
function timelineState () {
	var obj = {

 n: 3, // number of layers
    m:-1, // number of revisions
	data:null,				//added , modified, deleted
	colorsMse: new Array("#404ACF","#50CC3D","#CC5241"),
	colorsNo : new Array("#8FF", "#888", "#F88"),
	colorsHi : new Array("#A9A9A9","#EEE", "#CCC"),
	modType : new Array("addedFilesCount","modifiedFilesCount","deletedFilesCount"),
	currentLast : 0,
	currentFirst : 0,
	currentScale : 0,
	currenX : 0,
	currenY : 0,
	bars : null,
};
 obj.revisionsMap =[];
 obj.cleanedHist;
 obj.layers;
 obj.currentCanvas;
    
 obj.p = 30;
 obj.w, //size x
 obj.h, //size y
 obj.mx;
 obj.my;
 obj.mz;
return obj;
}

var xCl = function (o) { return function(d) { return d.x * o.w / o.mx; } }, // position of d
    y0Cl = function (o) {return function(d) { return o.h - d.y0 * o.h / o.my; } }, //height of 1st bar
    y1Cl = function (o) {return function(d) { 
		return o.h - (d.y + d.y0) * o.h / o.my; } 
	}, //height of 2nd bar
    y2Cl = function (o) {return function(d) { return d.y * o.h / o.mz; } }; // or `my` to not rescale, height of 3rd bar
   
function displayTimeline(hist, sizeX, sizeY, scale){
	stackedBarChart(hist,sizeX,sizeY,scale,hist.last,300,"#timelinechart",timeline_state);
}

function getRevisions (history,to,step,obj) {
	var revs = {}
	var i = to;
	for (i; i > to - step && i >= 0; --i) {
		var rev = obj.cleanedHist[i];
		revs[i] = rev;
	};
	return {
		first: i,
		revs: revs,
	};
}

function stackedBarChart (data,sizeX,sizeY,scale,version,step,canvas,obj) {

	var xe = xCl(obj);
	var y0e = y0Cl(obj);
	var y1e = y1Cl(obj);
	var y2e = y2Cl(obj);

	if (canvas)
		obj.currentCanvas = canvas;
	if (scale == null)
		scale = function (x) { return x; };
    d3.select(obj.currentCanvas).html("");
	$(obj.currentCanvas).html = "";
	obj.currentLast = version;
	obj.currentX = sizeX;
	obj.currentY = sizeY;
	obj.w = sizeX;
	obj.h = sizeY - .5 - obj.p;
	obj.cleanedHist = data;
	obj.currentScale = scale;
	
	var revsData = getRevisions(obj.cleanedHist,version,step,obj);
	var revs = revsData.revs;
		
	obj.currentFirst = revsData.first;
	
	obj.data = getData(revs, scale,obj);
	obj.m= obj.data[0].length;
	d3.layout.stack()(obj.data);
	
	for (var i = 0; i < obj.data.length; ++i) {
		for (var j = 0; j < obj.data[i].length; ++j) {
			obj.data[i][j].columnIndex = i;
		}
	}
	
	obj.mx = obj.m;
	
	obj.my = d3.max(obj.data, function(d) {
      return d3.max(d, function(d) {
       return d.y0 + d.y;
      });
    });

    //my=300;
    obj.mz = d3.max(obj.data, function(d) {
      return d3.max(d, function(d) {
        return d.y;
      });
    });

	
	//svg containing the whole graph
	var vis = d3.select(obj.currentCanvas)
		.append("svg:svg")
		.attr("width", obj.w)
		.attr("height", obj.h + obj.p);
	// set color of the bars
	
	
	vis.append("svg:text")
	.attr("class","timelineLabel")
	   .attr("x", obj.w-50)
	   .attr("y", obj.h+10)
	   .attr("dy", ".71em")
	   .attr("text-anchor", "middle")
	   .text("Rev."+obj.currentLast);

	vis.append("svg:text")
	   .attr("x", 45)
		.attr("class","timelineLabel")
	   .attr("y", obj.h+10)
	   .attr("dy", ".71em")
	   .attr("text-anchor", "middle")
	   .text("Rev."+obj.currentFirst);
	
	var cl1 = function (o) { 
			return function(d, i) { 
				return o.colorsMse[i]; 
			} 
		}(obj);
	
	obj.layers = vis.selectAll("g.layer")
		.data(obj.data)
		.enter().append("svg:g")
		.style("fill", cl1)
		.attr("class", "layer");



	//add bars 
	obj.bars = obj.layers.selectAll("g.bar")
		.data(function(d) { return d; })
		.enter().append("svg:g")
		.attr("class", "bar")
		.attr("transform", function(d) { return "translate(" + xe(d) + ",0)"; })
		.on("click", function(d, i) {
			//highlightCurrent(i,obj);
			/*projects[0][currentProject].getRevision(projects[0][currentProject], cleanedHist.i, function(rev){
				projects[0][currentProject].currRev = rev.revisionNumber;
			});*/
	});
	
	
 	//append rectangles to the bar	
	obj.bars.append("svg:rect").on("mouseover", function(d, i) { 
			var revisionNumber=obj.revisionsMap[i];
			var currentHistory = obj.cleanedHist[revisionNumber];
			if (!currentHistory)
				return;
			var modtype = obj.modType[d.columnIndex];
			tooltip.show(modtype + ": "+currentHistory[modtype] +"<br>Revision: " + revisionNumber + "<br>Author: " + currentHistory.author +"<br>Date: " + currentHistory.date+"");
		})
		.on("mouseout", function(d, i){
			tooltip.hide();
		})
		.attr("width", xe({x: .9}))
		.attr("x", 0)
		.attr("y", obj.h)
		.attr("height", 0)
		.transition()
		.delay(function(d, i) { return i * 10; })
		.attr("y", y1e)
		.attr("height", function(a,b) { return function(d) { return a(d) - b(d); } }(y0e,y1e));


	vis.append("svg:line")
		.attr("x1", 0)
		.attr("x2", obj.w - xe({x: .1}))
		.attr("y1", obj.h)
		.attr("y2", obj.h);
		
		function highlightCurrent(selected){
		 	var l = obj.layers.selectAll("g.layer rect")
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

//d3.select(window).on("keydown", 

/*eventHandler.add(window,"keydown",
function (o) {
	return function() {   
		var displacement = 0;  
		switch (d3.event.keyCode) {
			case 37: displacement = -200; break;
			case 39: displacement = 200; break;
			default:
				return;
		}
	repaint(displacement,300,o);
	}
}(obj));*/


var f = function (o) {
	return function() {   
		var displacement = 0;  
		switch (d3.event.keyCode) {
			case 37: displacement = -200; break;
			case 39: displacement = 200; break;
			default:
				return;
		}
		repaint(displacement,300,o);
	}
}(obj);

d3.select(window).on("keydown",f);

}

function repaint (displacement,minStep,obj) {
	var ver = obj.currentFirst+displacement;
	if (displacement > 0 && ver == obj.currentLast)
		return false;
	else if (displacement < 0 && ver == obj.currentFirst)
		return false;
		
	var last;
		
	if (ver < 0) {
		ver = 0;
		last = minStep;
	}
	else if (ver > obj.cleanedHist.last) {
		ver = obj.cleanedHist.last-minStep;
		last = obj.cleanedHist.last;
	}
	else {
		last = ver + minStep;
		if (last > obj.cleanedHist.last)
			last = obj.cleanedHist.last;
	}
	
	if (last == obj.currentLast && ver == obj.currentFirst)
		return false;
	
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
	stackedBarChart(obj.cleanedHist,obj.currentX,obj.currentY,obj.currentScale,last,minStep,obj.currentCanvas,obj);
	return true;
}


/*
 * format the history
 */
function getData(history, scale,obj){
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
			obj.revisionsMap[i] = rev;
			++i;
		}
	}	
	return result;
}


function highlightMse(obj){
	var group = d3.selectAll(obj.currentCanvas);
	
	group.selectAll("g.layer rect")
		.transition()
			.duration(500)
			.style("fill", function(d, i){
			var revisionNumber=obj.revisionsMap[d.x];
			var currentHistory = obj.cleanedHist[revisionNumber]; 
			if(currentHistory && currentHistory.hasMse)
				return obj.colorsMse[d.columnIndex];
			return obj.colorsHi[d.columnIndex];	
		});
		
	group.select("#highlight")
		.attr("onclick", "undoHighlight()");
}

function undoHighlight(obj){
	var group = d3.selectAll(obj.currentCanvas);
	
	group.selectAll("g.layer rect")
		.transition()
			.duration(500)
			.style("fill", function(d, i){
			var revisionNumber=obj.revisionsMap[d.x];
			var currentHistory = obj.cleanedHist[revisionNumber];
			if(currentHistory.hasMse)
				return obj.colorsMse[d.columnIndex];
			return obj.colorsMse[d.columnIndex];	
		});
		
	group.select("#highlight")
		.attr("onclick", "highlightMse()");
}
 

function transitionGroup(obj) {
  var group = d3.selectAll(currentCanvas);

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


function transitionStack(obj) {
  var stack = d3.select(currentCanvas);

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




