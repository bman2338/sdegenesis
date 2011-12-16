/*
* Helpers functions
*/

//Insert buttons in the timeline visualization
var insertButtonsForTimeline = function(){
	var name = timelineHandler.isPlaying ? "Pause" : "Play";
	
	var oldButtons = '<button id="group" class="first" onclick="transitionGroup()"> Group </button> <button id="stack" class="active last" onclick="transitionStack()">Stack</button><button id="highlight" class="active last" onclick="highlightMse()">toggle MSE</button><p>';
	var buttons = '<button id="backbutton" class="first" onclick="timelineHandler.backFunction()">back</button><button id="forwardbutton" class="active last" onclick="timelineHandler.forwardFunction()">forward</button><button id="playbutton" class="active last" onclick="timelineHandler.playFunction(this)">'+name+'</button>';
	$("#timelinechart").append("<div class=\"timelineController\">" + buttons + "</div>");
}

function forward(){
	var ret = repaint(200,300);
	if (ret)
		insertButtonsForTimeline();
	return ret;
}

function back(){
	var ret = repaint(-200,300);
	if (ret)
		insertButtonsForTimeline();
	return ret;
}

var timelineHandler = TimelineHandler();
timelineHandler.setBehavior(forward, back);

function countProperties(obj) {
	var count = 0;
	
	for(var prop in obj) {
		if(obj.hasOwnProperty(prop))
			++count;
		}
		
		return count;
}

var prefetchRevisions = function(prefetchRev, history){
	var prefetchRevisions = [];
	//alert(countProperties(history));
	for(var rev in history){
		//alert(JSON.stringify(history[i]));
		if(rev && rev.hasMse == "true"){
			alert("preso!");
			prefetchRevisions.push(rev);
			prefetchRev--;
			if(preferchRev == 0)
				break;
		}
	}
	
	for(var i = 0; i < prefetchRevisions.length; ++i){
		getRevision(proj, prefetchRevisions[i]);
	}
}