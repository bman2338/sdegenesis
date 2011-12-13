function VisualizationsRegister () {
	var register = []

	this.addVisualization = function (vis) {
		register.push(vis);
	}

	this.getVisualizations = function (data) {
		var results = [];
		for (var i = 0; i < register.length; ++i) {
			var vis = register[i];
			var toAdd = true;
			for (var j = 0; j < data.length; ++j) {
				if (!vis().allows(data[j])) {
					toAdd = false;
					break;
				}
			}
			if (toAdd)
				results.push(vis());
		}
		return results;
	}
	return this;
}
