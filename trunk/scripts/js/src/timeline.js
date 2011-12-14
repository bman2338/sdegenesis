var n = 3, // number of layers
    m, // number of revisions
	data,				//added , modified, deleted
	colorsMse = new Array("#404ACF","#50CC3D","#CC5241");
	colorsNo = new Array("#8FF", "#888", "#F88");
	colorsHi = new Array("#A9A9A9","#EEE", "#CCC");
	modType = new Array("addedFilesCount","modifiedFilesCount","deletedFilesCount");
	
var revisionsMap =[];
var cleanedHist;
    
var p = 20,
    w, //size x
    h, //size y
    mx,my,mz,
    x = function(d) { return d.x * w / mx; }, // position of d
    y0 = function(d) { return h - d.y0 * h / my; }, //height of 1st bar
    y1 = function(d) { return h - (d.y + d.y0) * h / my; }, //height of 2nd bar
    y2 = function(d) { return d.y * h / mz; }; // or `my` to not rescale, height of 3rd bar
   
function displayTimeline(histor, sizeX, sizeY, scale){

	w = sizeX;
	h = sizeY - .5 - p;
	//m = histor.length;
	cleanedHist = cleanHistory(hhistory); //not needed !change names
	data = getData(cleanedHist, scale);
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
	var layers = vis.selectAll("g.layer")
		.data(data)
		.enter().append("svg:g")
		.style("fill", function(d, i) { return colorsMse[i]; })
		.attr("class", "layer");



	//add bars 
	var bars = layers.selectAll("g.bar")
		.data(function(d) { return d; })
		.enter().append("svg:g")
		.attr("class", "bar")
		.attr("transform", function(d) { return "translate(" + x(d) + ",0)"; })
		.on("click", function(d, i) {
			highlightCurrent(i);
			
		//	$.ajax({
		//		url: '/get_data/'+projname+ '/'+i,
		//		success: function( recdata ) {
		//			var nodes = recdata.nodes;
		//			var edges = recdata.edges;
		//	}
		//})
	});
	
	
 	//append rectangles to the bar	
	bars.append("svg:rect").on("mouseover", function(d, i) { 
			var revisionNumber=revisionsMap[i];
			var currentHistory = cleanedHist[revisionNumber];
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
}


/*
 * format the history
 */
function getData(history, scale){
	var result = [];
	var added = [],
		modified = [],
		deleted = [];
	var i=0;
	
	for(var rev in history){
		added[i] = {x: i, y: scale(history[rev].addedFilesCount)};
		revisionsMap[i] = rev;
		i++;
	}
	result[0] = added;
	i=0;
	
	for(var rev in history){
		modified[i] = {x: i, y: scale(history[rev].modifiedFilesCount)};
		i++;
	}
	result[1] = modified;
	i=0;
	
	for(var rev in history){
		deleted[i] = {x: i, y: scale(history[rev].deletedFilesCount)};
		i++;
	}
	result[2] = deleted;
	
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
			if(currentHistory.hasMse)
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




