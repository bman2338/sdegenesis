
function hasLineFeed(s){
	var regexp = /\\n/;
	return regexp.test(s);
}

function isUrl(s) {
	var regexp = /http:\/\/[A-Za-z0-9\.-]{3,}\.[A-Za-z]{3}/;
	return regexp.test(s);
}


function clickNew(){
	document.getElementById("content").innerHTML = '<form name="addProject" action="" method="post"> '+ 
												'<table><tr>'+
												'<td>Project Name:</td>'+
												'<td><input type="text" id="projectName" name="project[name]"/></td>'+
												'</tr><tr>'+
												'<td>Project SVN:</td>' +
												'<td><input type="text" id="svnurl" name="project[svn]"/></td>'+
												'</tr><tr>'+
												'<td>Project Bugtracker</td>'+
								'<td><input type="text" id="bugtracker" name="project[bugtracker]"/></td>'+
							'</tr></table><button type="submit" value="Send">Submit request</button></form>';
}
