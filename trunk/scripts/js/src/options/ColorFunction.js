function typeColor (graph) {
	var colors = {
		"Method": [0.2,0.8,0.2],
		"Class": [0.8,0.2,0.2],
		"Attribute": [0.2,0.2,0.8],
	};
	var currentColor = [0.8,0.0,0.2];
	return {
		name: "Color for node type",
		preEvalFun: function (node) {
			return function (node) {
				var color = currentColor;
				if (colors[node.properties.ElementType]) {
					color = colors[node.properties.ElementType]
				}
				else {
					alert(node.properties.ElementType);
					color = currentColor;
					colors[node.properties.ElementType] = color;
					currentColor[1] = currentColor[1] + 0.2;
				}
				return d3.rgb(255*color[0],255*color[1],255*color[2]);
			}
		},
	};
}
