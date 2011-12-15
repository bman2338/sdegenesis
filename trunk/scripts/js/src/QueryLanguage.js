function trim(stringToTrim) {
	return stringToTrim.replace(/^\s+|\s+$/g,"");
}

function getQueryResults (query,types,graph) {
	var results = {};
	var splits = query.split(";");
	for (var tokenId in splits) {
		var str = trim(splits[tokenId]);
		
		var tokens = str.split(":");
		if (tokens.length != 2)
				continue;
				
		var keyword = trim(tokens[0]).toLowerCase();
		if (keyword.length > 1)
			keyword = keyword[0].toUpperCase()+keyword.substring(1);
		var argument = trim(tokens[1]);
		
		if (types.indexOf(keyword) != -1) {
			var nodes = graph.getNodeSelection(function (node) {
				var toSelect = node.properties.ElementType == keyword && node.properties.name.match(argument) != null;
				return toSelect;
			});
			results[keyword] = nodes;
		}
	}
	return results;
}