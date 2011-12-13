var t = 1, 
	v = 70, 
	size = 600,
 	data = d3.range(size).map(next); // starting dataset
 
var w = 2,
	h = 80;
 
function next(revisions) {
	return {
 		rev: ++t, //rev num revision.number
 		value: v = ~~Math.max(10, Math.min(90, v + 10 * (Math.random() - .5))) // rev data
 	};
}
 
 //function for updating the graph every 10 sec
//setInterval(function() {
//	data.shift();
//	data.push(next());
//	redraw();
//}, 10000);

function drawTimeline(project){
	
var x = d3.scale.linear()
	.domain([0, 1])
	.range([0, w]); 

var y = d3.scale.linear()
	.domain([0, 100])
	.rangeRound([0, h]);
	
var chart = d3.select("body")
	.append("svg:svg")
	.attr("class", "timeline")
	.attr("width", w * data.length - 1)
	.attr("height", h);
	
chart.selectAll("rect")
	.data(data)
	.enter().append("svg:rect")
	.attr("x", function(d, i) { return x(i) - .5; })
	.attr("y", function(d) { return h - y(d.value) - .5; })
	.attr("width", w)
	.attr("height", function(d) { return y(d.value); })
	.on("mouseover", function(d, i) { 
		tooltip.show("Revision: " + i + "<br>Author: " /*+ d.author*/ +"<br>Date: "/* + d.date*/+"");
	})
	.on("mouseout", function(d, i){
		tooltip.hide();
	})
	.on("click", function(d, i) {
		//alert("cippo" + projname + " rev "+ i);
		$.ajax({
 			url: '/get_data/'+projname+ '/'+i,
 			success: function( data ) {
   				var nodes = data.nodes;
   				var edges = data.edges;
   			}
 		})
	});
	
chart.append("svg:line")
	.attr("x1", 0)
	.attr("x2", w * data.length)
	.attr("y1", h - .5)
	.attr("y2", h - .5)
	.attr("stroke", "#000");
	
}
	
function redraw() {
	// Update…
	chart.selectAll("rect")
	.data(data)
	.transition()
	.duration(1000)
	.attr("y", function(d) { return h - y(d.value) - .5; })
	.attr("height", function(d) { return y(d.value); });
}
	
var tooltip=function(){
 var id = 'tt';
 var top = 3;
 var left = 3;
 var maxw = 300;
 var speed = 10;
 var timer = 20;
 var endalpha = 95;
 var alpha = 0;
 var tt,t,c,b,h;
 var ie = document.all ? true : false;
 return{
  show:function(v,w){
   if(tt == null){
    tt = document.createElement('div');
    tt.setAttribute('id',id);
    t = document.createElement('div');
    t.setAttribute('id',id + 'top');
    c = document.createElement('div');
    c.setAttribute('id',id + 'cont');
    b = document.createElement('div');
    b.setAttribute('id',id + 'bot');
    tt.appendChild(t);
    tt.appendChild(c);
    tt.appendChild(b);
    document.body.appendChild(tt);
    tt.style.opacity = 0;
    tt.style.filter = 'alpha(opacity=0)';
    document.onmousemove = this.pos;
   }
   tt.style.display = 'block';
   c.innerHTML = v;
   tt.style.width = w ? w + 'px' : 'auto';
   if(!w && ie){
    t.style.display = 'none';
    b.style.display = 'none';
    tt.style.width = tt.offsetWidth;
    t.style.display = 'block';
    b.style.display = 'block';
   }
  if(tt.offsetWidth > maxw){tt.style.width = maxw + 'px'}
  h = parseInt(tt.offsetHeight) + top;
  clearInterval(tt.timer);
  tt.timer = setInterval(function(){tooltip.fade(1)},timer);
  },
  pos:function(e){
   var u = ie ? event.clientY + document.documentElement.scrollTop : e.pageY;
   var l = ie ? event.clientX + document.documentElement.scrollLeft : e.pageX;
   tt.style.top = (u - h) + 'px';
   tt.style.left = (l + left) + 'px';
  },
  fade:function(d){
   var a = alpha;
   if((a != endalpha && d == 1) || (a != 0 && d == -1)){
    var i = speed;
   if(endalpha - a < speed && d == 1){
    i = endalpha - a;
   }else if(alpha < speed && d == -1){
     i = a;
   }
   alpha = a + (i * d);
   tt.style.opacity = alpha * .01;
   tt.style.filter = 'alpha(opacity=' + alpha + ')';
  }else{
    clearInterval(tt.timer);
     if(d == -1){tt.style.display = 'none'}
  }
 },
 hide:function(){
  clearInterval(tt.timer);
   tt.timer = setInterval(function(){tooltip.fade(-1)},timer);
  }
 };
}();	