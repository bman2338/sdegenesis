function finalStroke (graph) {
	return {
		name: "Stroke if final",
		evalFun: function (node) {
			if (node.properties.isFinal)
				return d3.rgb(0,0,0);
			return d3.rgb(255,255,255);
		}
	}
}