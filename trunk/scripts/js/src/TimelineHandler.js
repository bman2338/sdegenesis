function TimelineHandler () {
	var obj = {};
	obj.interval = 1500;
	obj.playInterval = 0;
	obj.forwardFunction = function () { };
	obj.backFunction = function () { };
	obj.playFunction = function () { 
		playInterval = setInterval(function() {
			obj.forwardFunction();
		}, obj.interval);
	};
	obj.pauseFunction = function () { 
		obj.playInterval = clearInterval(obj.playInterval);
	};
	
	obj.setBehavior = function (f,b) {
		obj.forwardFunction = f;
		obj.backFunction = b;
	}
	return obj;
}

function forward(){
	repaint(200, 300);
}

function back(){
	repaint(-200, 300);
}