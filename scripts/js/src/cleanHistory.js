	function cleanHistory(hist, target) {
		
		var newHist = {};
		if(target) {
			newHist = target;
		}
	
		for(var e in hist) {
			if("_id" == e)  {
				continue;
			}
			var entry = hist[e];
			entry.author = entry.author[0];
			entry.date = entry.date[0];
			entry.addedFilesCount = entry.addedFilesCount[0];
			entry.deletedFilesCount = entry.deletedFilesCount[0];
			entry.modifiedFilesCount = 	entry.modifiedFilesCount[0];
			entry.project = entry.project[0];
			entry.hasMse = entry.hasMse[0];

			var iter = [ "deletedFiles", "addedFiles", "modifiedFiles" ];

			for(var it in iter) {
				var list = entry[iter[it]];
				for(var le in list) {
					list[le] = list[le][0];
				}
			}
			newHist[e] = entry;
		}
		return newHist;
	};
