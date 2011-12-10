function Visualization () {

	this.draw = function () {
		// function to draw
	};

	this.elements = {
	}

	this.options = {
	}

	return this;
}

function Graph () {

	var nodes = [];
	var edges = [];

	this.draw = function () {
		// Draw the graph..
	}

	this.elements = {
		node: {
			allow: function() { return true; },
			sizeFunction: function () {
				// define size
			},
			colorFunction: function () {
				// function for color
			},
		},
		edge: {
		},
	};


	this.options = {	
	}	

	return this;
}
