module.exports = {
	cleanHistory : function(hist, target) {
		
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

            newHist[e] = this.cleanFileOps(entry);
		}
        
        
		return newHist;
	},

    cleanFileOps : function(entry) {
         var iter = [ "deletedFiles", "addedFiles", "modifiedFiles" ];
			for(var it in iter) {
				var fileOp = entry[iter[it]];
                if(fileOp)
                    this.cleanFileOp(fileOp);
			}    
        return entry;
    },
    
    cleanFileOp : function(fileOp) {
        for(var le in fileOp) {
					fileOp[le] = fileOp[le][0];
				}
    },

	historyToD3Format : function(hist) {
		var data = {};
		var dates = [];
		var format = d3.time.format(this.getHistoryDateFormatString())
		for(var e in hist) {
			var entry = hist[e];
			entry.number = e;
			var dateStr = this.getDateStrNoZone(entry.date);
			
			
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
	},
	
	getDateStrNoZone : function(dateStr) {
		var arr = dateStr.split(" ");
		var res = "";
		arr[4] = " ";

		return res.concat(arr);
	},

	getHistoryDateFormatString : function(){
		return "%a,%b,%d,%X, ,%Y";
	},
	
	getD3DataFormatString : function() {
		return "%Y-%m-%d";
	},
}
	