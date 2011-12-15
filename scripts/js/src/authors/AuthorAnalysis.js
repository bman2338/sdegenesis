var authorColorGlobal = {};
authorColorGlobal["*"] = d3.rgb(200, 200, 200);


var getBestAuthor = function(entries) {
	    var best = null;
         var bestRank = -1;
    
        for(var e in entries) {
            var entry = entries[e];
            var currentRank = entry.addedFilesCount + entry.modifiedFilesCount + 0.5*entry.deletedFilesCount;
            if(bestRank < currentRank) {
                best = entry.author;
                bestRank = currentRank;
            }
			else if (bestRank == currentRank) {
				if (best.author > currentRank.author)  {
					best = entry.author;
					bestRank = currentRank;
				}
			}
        }

		return best;
}

function getGlobalAuthorColor (author) {
	var color = authorColorGlobal[author];

    if(!color) {
        color = d3.rgb(55 + 200*Math.random(), 100 + 155*Math.random(), 255*Math.random());
        authorColorGlobal[author] = color;
    }
    return color;
}

var authorGraphColorFunction = function () {
	return {
		evalFun: function(node) {
			return getGlobalAuthorColor(node.name);
		}
	};
};

var authorColorFunction = function(revEntries) {
	return {
		evalFun: function(entries) {
        if(!entries) {
            return d3.rgb(255, 255, 255);
        }

     	var best = getBestAuthor(entries);
        
        
    
        if(!best) {
            best = "*";
        }
    
        var color = getGlobalAuthorColor(best);
        return color;
    	},
	}
};

var collaborationsSizeFunction = function () {
	return {
		evalFun: function(node) {
			return Math.min(30,node.numberOfActions);
		},
	};
};

function getAuthorLabel (authorName) {
	var color = getGlobalAuthorColor(authorName);
	return "<div class='AuthorLabel' style='background-color:" + color + "'>" + authorName + "</div>";
}

var mouseOverAuthorNode = function () {
	return {
		evalFun: function (node) {
			var label = getAuthorLabel(node.name);
			label += "Number of contributions: " + node.numberOfActions;
			tooltip.show(label);
		}
	}
}
