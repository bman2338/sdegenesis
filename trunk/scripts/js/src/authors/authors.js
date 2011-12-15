function getFileToAuthorMap(hist) {
	var files = {};
	for(var r in hist) {
		var revision = hist[r];
		if(revision.modifiedFiles) {
			var mFiles = revision.modifiedFiles;
			for(var mf in mFiles) {
				var file = mFiles[mf];
				if(!files[file]) {
					files[file] = {};
				}
				
				if(!files[file][revision.author]) {
					files[file][revision.author] = 0;
				}
				
				files[file][revision.author] += 1;
			} 
		}
	}
	return files;
}



function getCoauthorshipMatrix(hist) {
	var files = getFileToAuthorMap(hist);
	var coauthorShipMatrix = {};
	var maxVal = 0;
	
	
	for(var f in files) {
		var file = files[f];
		for(var i in file) {
			var valI = file[i];
			if(!coauthorShipMatrix[i]) {
				coauthorShipMatrix[i] = {};
			}
			
			for(var j in file) {
				var valJ = file[j];
					if(!coauthorShipMatrix[j]) {
						coauthorShipMatrix[j] = {};
					}
				
				if(!coauthorShipMatrix[i][j]) {
					coauthorShipMatrix[i][j] = 0;
					coauthorShipMatrix[j][i] = 0;
				}
				
				var value = (valI + valJ)/2;
				coauthorShipMatrix[i][j]+= value;
				coauthorShipMatrix[j][i]+= value;
				maxVal = Math.max(maxVal, coauthorShipMatrix[i][j]);
			}
			
		}
	}
	coauthorShipMatrix.__maxVal = maxVal;
	return coauthorShipMatrix;
}

function convertToCoauthorship(hist) {
	var mat = getCoauthorshipMatrix(hist);
	var authorArray = [];
	var max=mat.__maxVal;
	for(var a in mat) {
		authorArray.push(a);
	}
	
	return { data: authorArray, 
			 metric: function(a1, a2) { 
				var rel = 0;
				if(mat[a1] && mat[a1][a2]) {
					rel = mat[a1][a2];
				}
				
				return 1 - rel/max;
			}
		};
}


