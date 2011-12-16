/*
* Helpers functions
*/

//Insert buttons in the timeline visualization
var insertButtonsForTimeline = function(){
	var buttons = '<button id="group" class="first" onclick="transitionGroup()">
	       Group
	     </button>
	     <button id="stack" class="active last" onclick="transitionStack()">
	       Stack
	     </button><button id="highlight" class="active last" onclick="highlightMse()">
	       toggle MSE
	       </button><p>
	       <button id="backbutton" class="first" onclick="back()">
	           back
	         </button>
	         <button id="playbutton" class="active last" onclick="play()">
	             Play
	         </button>
	         <button id="forwardbutotn" class="active last" onclick="forward()">
	           forward
	       </button>';
	document.getElmentById('timelinechart').innerHTML += buttons;
}