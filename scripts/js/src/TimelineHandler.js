function TimelineHandler () {
	var obj = {};
	obj.interval = 4000;
	obj.playInterval = 0;
	obj.forwardFunction = function () { };
	obj.backFunction = function () { };
	obj.isPlaying = false;
			
	obj.playFunction = function (button) {
		document.getElementById(button.id).innerHTML="Pause";
		obj.isPlaying = true;	
		if (obj.forwardFunction()) {
			obj.playInterval = setInterval(function() {
				if (!obj.forwardFunction()){
					obj.pauseFunction(button);
				}
				}, obj.interval);	
		}
		else {
			obj.isPlaying = false;
			document.getElementById(button.id).innerHTML="Play";
		}
	};
	obj.pauseFunction = function (button) { 
		document.getElementById(button.id).innerHTML="Play";	
		obj.playInterval = clearInterval(obj.playInterval);
	};
	
	obj.setBehavior = function (f,b) {
		obj.forwardFunction = f;
		obj.backFunction = b;
	};
	return obj;
}

function nextRevision () {
	
}

function previousRevision () {
	
}