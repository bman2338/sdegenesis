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
		
		var regexp = new RegExp(argument,"gi");
		
		var l = 0
		for (var i = 0; i < argument.length; ++i) {
			var c = argument[i];
			if ((c >= 'A' && c <= 'z') || (c >= 0 && c <= 9) || c == '_')
				l += 1;
		}
		
		if (types.indexOf(keyword) != -1) {
			var nodes = graph.getNodeSelection(function (node) {
				var toSelect = node.properties.ElementType == keyword;
				if (!toSelect)
				 	return false;
				
				var matchResult = node.properties.name.match(argument);
				if (matchResult == null || matchResult.length == 0 || matchResult[0] == "")
					return false;
				var res = 0;
				for (var i = 0; i < matchResult.length; ++i)
					res += matchResult[i].length;
				return l <= res;
			});
			results[keyword] = nodes;
		}
	}
	return results;
}