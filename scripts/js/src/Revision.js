//Revision object
function Revision(projectName, revisionNumber){
	this.projectName = projectName;
	this.revisionNumber = revisionNumber;
	this.graph = undefined;
	
	//get asynchronously the list of deleted files
	//from the server if not already stored
	this.getDeletedFiles = function() {
		if(this.deletedFilesCount == undefined){
			$.ajax({
				url: 'http://127.0.0.1:1234/get_data/'+ this.projectName +'/history/deleted_files/' + this.revisionNumber,
				error: function (data ){
					alert("Some problem happened: " + JSON.stringify(data));
				},
				success: function( data ) {
					this.deletedFilesCount = data.deletedFiles;
					return this.deletedFilesCount;
				}
			});
		}
		else {
			return this.deletedFiles;
		}
	};
	
	this.getAddedFiles = function() {
		if(this.addedFilesCount == undefined){
			$.ajax({
				url: 'http://127.0.0.1:1234/get_data/'+ this.projectName +'/history/added_files/' + this.revisionNumber,
				error: function (data ){
					alert("Some problem happened: " + JSON.stringify(data));
				},
				success: function( data ) {
					this.addedFilesCount = data.addedFiles;
					return this.addedFilesCount;
				}
			});
		}
		else {
			return this.addedFiles;
		}
	};
	
	this.getModifiedFiles = function() {
		if(this.modifiedFiles == undefined){
			$.ajax({
				url: 'http://127.0.0.1:1234/get_data/'+ this.projectName +'/history/modified_files/' + this.revisionNumber,
				error: function (data ){
					alert("Some problem happened: " + JSON.stringify(data));
				},
				success: function( data ) {
					this.modifiedFiles = data.modifiedFiles;
					return this.modifiedFiles;
				}
			});
		}
		else {
			return this.modifiedFiles;
		}
	};
	
	this.getGraph = function(){
		return this.graph;
	}
}