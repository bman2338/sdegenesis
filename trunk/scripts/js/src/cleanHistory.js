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

            newHist[e] = cleanFileOps(entry);
		}
        
        
		return newHist;
	};

    function cleanFileOps(entry) {
         var iter = [ "deletedFiles", "addedFiles", "modifiedFiles" ];
			for(var it in iter) {
				var fileOp = entry[iter[it]];
                if(fileOp)
                    cleanFileOp(fileOp);
			}    
        return entry;
    }
    
    function cleanFileOp(fileOp) {
        for(var le in fileOp) {
					fileOp[le] = fileOp[le][0];
				}
    }

	function historyToD3Format(hist) {
		var data = {};
		var dates = [];
		var format = d3.time.format(getHistoryDateFormatString())
		for(var e in hist) {
			var entry = hist[e];
			entry.number = e;
			var dateStr = getDateStrNoZone(entry.date);
			
			
			var fd = format.parse(dateStr);
            
            fd.setMinutes(0);
            fd.setHours(0);
            fd.setSeconds(0);
            
			var date = format(fd);
			if(!data[date]) {
				data[date] = [];
				dates.push(date);
			}
			//TODO make copy
			data[date].push(entry);
			
		}
		return { "dates": dates, "data" : data };
	}
	
	function getDateStrNoZone(dateStr) {
		var arr = dateStr.split(" ");
		var res = "";
		arr[4] = " ";

		return res.concat(arr);
	}
	

	function getHistoryDateFormatString() {
		return "%a,%b,%d,%X, ,%Y";
	}
	
	function getD3DataFormatString() {
		return "%Y-%m-%d";
	}
	