/*
* Genesis server side
*
* @autor: Genesis Team
*/

//import frameworks
var express = require('express')
  , app = require('express').createServer()/*express.createServer(
	express.logger(),
		express.cookieParser(),
		express.session({ secret: 'keyboard cat' })
	)*/
  , io = require('socket.io').listen(app)
  , mongoose = require('mongoose')
  , schemas = require('./schemas')
  , Schema   = mongoose.Schema
  , jade = require('jade')
  , mongo = require('mongoskin');

var url = require('url')
  , fs = require ('fs');

var cleaner = require('./cleanHistoryServerSide');

var mongoskin = mongo.db("localhost:8888/genesis_db");

app.listen(8079);
app.configure(function(){
	app.use(express.bodyParser());
	app.use(express.logger());
	app.use(express.cookieParser());
	app.use(express.session({ secret: 'keyboard cat' }));
	app.use(express.errorHandler({showStack: true, dumpExceptions: true}));
});

//Database connections
var db = mongoose.connect('mongodb://localhost:8888/genesis_db');
var UserSchema = db.model('User');
var ProjectSchema = db.model('Project');
var NodeSchema = db.model('Node');
var EdgesSet = db.model('EdgesSet');
var Property = db.model('Property');
var Relation = db.model('Relation');

//Redirects

/*
* Home Page
* Points to:
*	- Register
*	- Login
*/

/*
* Redirect for the splash screen
*/
app.get('/', function (req, res) {
	res.render(__dirname + '/pages/index.jade', {
		
	});
});

/*
* Login post handlers
*/
app.post('/login', function (req, res) {
	console.log("inside login post");
	//variables to render the page when login is done
	loginUser(req.body.user.name, req.body.user.pass, function(result){
		if(result[0] != undefined && result[0].Password == req.body.user.pass){
			console.log("login successfull");

			//Setting in the cookies some variable useful later
			req.session.userAuthenticated = true;
			req.session.username = result[0].Username;
			req.session.projects = result[0].Projects;
			
			var usr = {
				name: result[0].Username,
				projects: result[0].Projects,
			};
			console.log(JSON.stringify(usr));
			mongo.db("localhost:8888/genesis_db?auto_reconnect").collection("projects").find({open: 1}).toArray(function(err, openProjs){
				
				for(var i = 0; i < openProjs.length; i++){
					console.log(openProjs[i]);
					//if it's not in usr projects, add it
					if(checkProjects(usr.projects, openProjs[i])){
						openProjs[i].defaultShared = true;
						usr.projects.push(openProjs[i]);
					}
				}
				console.log(JSON.stringify(usr));
				//render the management with user connected
				res.render(__dirname + '/pages/management.jade', {
					userInfo: usr,
				});
			});
		}
		else{
			console.log("login not successfull");
			req.session.userAuthenticated = null;
			
			//render the index with the user not logged
			res.render(__dirname + '/pages/index.jade', {
				
			});
		}
	});
});

/*
* Function to check if the user has already
* this open project inside his personal projects
*/
function checkProjects(userProjects, projectToAdd){
	for(var i = 0; i < userProjects; i++){
		console.log(projectToAdd.name + " " + userProjects[i].name)
		if(userProjects[i].name == projectToAdd.name)
			return false;
	}
	return true;
}

/*
* Registration post handler
*/
app.post('/register', function (req, res) {
	registerUser(req, function(result){
		//variables to render the page when registration is done
			if(result){
				//Setting in the cookies some variable useful later
				req.session.userAuthenticated = true;
				req.session.username = req.body.user.name;
				req.session.projects = [];
				
				//create an user object
				var usr = {
					name: req.session.username,
					projects: req.session.projects
				};
				
				//render management with user info
				res.render(__dirname + '/pages/management.jade', {
					userInfo: usr,
				});
				
			}
			else{
				//problem with the registration, do something!
				req.session.userAuthenticated = null;
				//pass some error to index.jade
				res.render(__dirname + '/pages/index.jade', {
					
				});
			}
		});
});

/*
* Management get and post handler
*/
app.get('/management', function(req, res) {
	if(req.session.userAuthenticated){
		//return the page
		res.render(__dirname + '/pages/management.jade', {
			userProjects: req.session.projects,
		});
	}
	//user not logged in, redirected to index
	else{
		res.render(__dirname + '/pages/index.jade', {
			
		});
	}
});

/*
* Logout get handler
*/
app.get('/logout', function(req, res){
	//set session variables to logout the user
	req.session.userAuthenticated = false;
	req.session.usernamename = undefined;
	req.session.projects = undefined;
	
	//send the user to the index
	res.render(__dirname + '/pages/index.jade', {
		
	});
});

/*
* Handler for adding a project. It sends a request through a socket
* to the scala service, moreover adds the project to the list of 
* projects of the user.
*/
app.post('/addProject', function(req, res){
	var projectName = req.body.project.name;
	var projectrepo = req.body.project.repo;
	var repoType = req.body.project.repoType.toLowerCase() || "";
	var projectBugTracker = req.body.project.bugtracker || "";
	var bugtrackerType = req.body.project.btType.toLowerCase() || "";
	var projectNameBugtracker = req.body.project.projectnamebugtracker || "";
	var fromRev = req.body.project.fromRev;
	var toRev = req.body.project.toRev || "";
	var stepRev = req.body.project.stepRev;
	var repoUsername = req.body.project.repoUsername || "";
	var repoPass = req.body.project.repoPassword || "";
	var BTUsername = req.body.project.btUsername || "";
	var BTPass = req.body.project.btPassword || "";
	
	mongo.db("localhost:8888/genesis_db?auto_reconnect").collection("projects").find({name: projectName}).toArray(function(err, projects){
		console.log("prima del for con username " + req.session.username);
		console.log("RECEIVED: projectName> "+ projectName + 
		"\n projectRepo> " + projectrepo + 
		"\n repoType> " + repoType + 
		"\n projectBugTracker> " + projectBugTracker + 
		"\n bugTrackerType> " + bugtrackerType + 
		"\n projectBugTrackerName> " + projectNameBugtracker +
		"\n from> " + fromRev +
		"\n to> " + toRev + 
		"\n step> " + stepRev + 
		"\n repoUsername> " + repoUsername + 
		"\n repoPass> " + repoPass +
		"\n btUsername> " + BTUsername +
		"\n btPassword> " + BTPass);
		var found = false;
		for(var i = 0; i < projects.length; i++){
			if(projects[i].name == projectName && projects[i].username == req.session.username){
				//this user has already a project with this name
				console.log("WARNING: trying to add a project that already is in the database under this user");
				found = true;
			}
		}
		if(!found){
				//make the scala backend do something
				//FOR NOW ITS COMMENTED BECAUSE THE SERVICE IS NOT ON THE SERVER
				var net = require('net');
				var client = net.createConnection(6969, '172.16.224.88');
                
                client.write(
					"projectName> "+ projectName + 
					"\n projectRepo> " + projectrepo + 
					"\n repoType> " + repoType + 
					"\n projectBugTracker> " + projectBugTracker + 
					"\n bugTrackerType> " + bugtrackerType + 
					"\n projectBugTrackerName> " + projectNameBugtracker +
					"\n from> " + fromRev +
					"\n to> " + toRev + 
					"\n step> " + stepRev + 
					"\n repoUsername> " + repoUsername + 
					"\n repoPass> " + repoPass +
					"\n btUsername> " + BTUsername +
					"\n btPassword> " + BTPass);
				client.end();
				
				//add the new project to the db
				var projToAdd = {
					name : projectName,
					status: "updated",
					revisions: [],
					open: 0,
				};
				mongoskin.collection("projects").insert(projToAdd, function(err){
					if(err){
						console.log(err);
					}
					else {
						if(req.session.projects){
							console.log("projects not empty, adding the newly added project to the array")
							req.session.projects.push(projToAdd);
						}
						else{
							console.log("projects empty, creating new array and saving it")
							req.session.projects = new Array();
							req.session.projects.push(projToAdd);
						}
					}
					var usr = {
						name: req.session.username,
						projects: req.session.projects,
					};
					console.log("username in session: " + req.session.username + " projects in session: " + JSON.stringify(req.session.projects))
					//return the management page with the updated list of projects for the user
					res.render(__dirname + '/pages/management.jade', {
						userInfo: usr,
					});
					
					//update the list of projects for the user, in the meantime
					mongoskin.collection("users").update({Username: req.session.username}, {$push: {Projects: projToAdd}}, function(err, user){
						console.log("In the meantime, user updated with new project");
					});
				});
			
		}
	});
});

/*
* Save the project sharing settings and redirect to management.jade
*/
app.post('/save_project/:projectname', function(req, res) {
	
});

/*
* Delete the project. If it's global, remove it from user's array, otherwise also from DB.
* Return the user to the management.jade page
*/
app.get('/delete_project/:projectname', function(req, res){
	var projectName = req.params.projectname;
	if(req.session.userAuthenticated){
		//user connected, check if he/she has the project
		mongo.db('localhost:8888/genesis_db?auto_reconnect').collection('users').find({"Username" : req.session.username}).toArray(function (err, user){
			var projs = user[0].Projects;
			for(var i = 0; i < projs.length; i++){
				if(projs[i].name == req.params.projectname){
					console.log("found the project with this name");
					//found! check if it's private or public
					if(projs[i].open == 0){
						console.log("project closed");
						//private, delete everything
						//first from the db itself
						var revisions = projs[i].revisions;
						for(var j = 0; j < revisions.length; j++){
							mongo.db('localhost:8888/genesis_db?auto_reconnect').collection(projectName + '_rev' + revisions[j] + '_nodes').drop();
							mongo.db('localhost:8888/genesis_db?auto_reconnect').collection(projectName + '_rev' + revisions[j] + '_edges').drop();
						}
						
						//from the projects array
						mongo.db('localhost:8888/genesis_db?auto_reconnect').collection('projects').remove({"name" : projectName});
						
						//then from the list of the user
						mongo.db('localhost:8888/genesis_db?auto_reconnect').collection('users').update({"Username" : req.session.username}, {$pull : {"Projects" : {"name" : projectName}}}, function(){
							var usr = {
								name: req.session.username,
								projects: req.session.projects,
							};
							//render the management with user connected
							res.render(__dirname + '/pages/management.jade', {
								userInfo: usr,
							});
						});
					}
					else {
						//public, remove it ONLY from the list of the user
						console.log("project open");
						mongo.db('localhost:8888/genesis_db?auto_reconnect').collection('users').update({"Username" : req.session.username}, {$pull : {"Projects" : {"name" : projectName}}}, function(){
							var usr = {
								name: req.session.username,
								projects: req.session.projects,
							};
							//render the management with user connected
							res.render(__dirname + '/pages/management.jade', {
								userInfo: usr,
							});
						});
					}
				}
			}
		});
	}
});

/*
* Redirect for showing the project
*/
app.get('/show_project/:projectname', function(req, res){
	
	mongo.db('localhost:8888/genesis_db?auto_reconnect').collection('projects').find({"name": req.params.projectname}).toArray(function (err, proj){
			
			var newJson = {};
			newJson.projectName = req.params.projectname;
			newJson.revisions = proj[0].revisions;
			
			//newJson.name = nodes.project;
			newJson.children = [];
			
			var usr = {
				name: req.session.username,
				projects: req.session.projects
			};
			
			console.log("PROJECT IN SESSION VAR : " + req.session.projects);
			
			//nodes: and edges: will be dropped soon
			res.render(__dirname + '/pages/viz.jade', {
				userInfo: usr,
				vizJson: newJson,
				nodes: {},
				edges: {},
			});
		});
});

/*
* Redirect for the AJAX request of the revisions
*/
app.get('/get_data/:projectname/:rev', function(req, res){
	console.log(req.params.projectname + '_rev' + req.params.rev + '_edges');
	mongo.db('localhost:8888/genesis_db?auto_reconnect').collection(req.params.projectname + '_rev' + req.params.rev + '_edges').find().toArray(function(err, edges){
		mongo.db('localhost:8888/genesis_db?auto_reconnect').collection(req.params.projectname + '_rev' + req.params.rev + '_nodes').find().toArray(function(err, nodes){
			//SEND BACK VIA AJAX THE RESULTS nodes[0] and edges[0]
			//if (req.xhr) {
		    // respond with the each user in the collection
		    // passed to the "user" view
			if(nodes[0]){
				res.send({
					nodes: nodes[0].nodes,
					edges: edges[0].edges,
				});
			}
			else {
				res.send({
					nodes: "ERROR",
					edges: "ERROR",
				});
			}	
			//}
		});
	});
});

/*
* Route for the AJAX requests for the histories
*/
app.get('/get_history/:projectname', function(req, res){
	//history to pass at the beginning
	target = {}
	
	var nmn_connect = require('mongoose/node_modules/mongodb').connect;

	nmn_connect('mongo://localhost:8888/genesis_db?auto_reconnect', function(err, db) {
		var interestingCollections = new Array();
		var numberOfColl = 0;
		var counter = 0;
		var biggest = 0;
		if(!err){
			db.collectionNames(function(err, names){
				if(!err){
					for(var i = 0; i < names.length; i++){
						var collName = names[i].name.slice(names[i].name.indexOf('.') + 1);
						var projNameFromCollName = collName.slice(0, collName.indexOf('_'));
						var typeOfColl = collName.slice(collName.indexOf('_') + 1, collName.lastIndexOf('_'));
						//check if the collection is for our project
						if(projNameFromCollName == req.params.projectname && typeOfColl == "history"){
							var revNum = names[i].name.slice(names[i].name.lastIndexOf('_') + 1);
							if(biggest < parseInt(revNum))
								biggest = parseInt(revNum);
							//the collection on which I want to iterate!! :) and it's from the history!
							interestingCollections.push(collName);
						}
					}
					
					numberOfColl = interestingCollections.length;
					for(var i = 0 ; i < interestingCollections.length; i++){
						//console.log(interestingCollections[i]);
						mongo.db('localhost:8888/genesis_db?auto_reconnect').collection(interestingCollections[i]).find({}, {modifiedFiles : 0, addedFiles: 0, deletedFiles: 0}).toArray(function(err, hist){
							
							//get that history, give it to the cleaner, save it
							//back in the target, continue with the next
							target = cleaner.cleanHistory(hist[0], target);
							counter = counter + 1;
							target.last = biggest;
							if(counter == numberOfColl){
								//console.log(JSON.stringify(target));
								res.send({
									history : target,
								});
							}
						});
					}
				db.close();
				}
			});     
		}else{
			console.log(err)
		}
	});
});

/*
* Route for the AJAX request for the deleted files 
* in one history for a specified project
*/
app.get('/get_data/:projectname/history/deleted_files/:rev', function(req, res){
	var searchedRevision = req.params.rev;
	var smallestFound = 9007199254740992; //max integer in javascript
	var nmn_connect = require('mongoose/node_modules/mongodb').connect;
	
	nmn_connect('mongo://localhost:8888/genesis_db?auto_reconnect', function(err, db) {
		if(!err){
			db.collectionNames(function(err, names){ // what I was looking for
				if(!err){
					for(var i = 0; i < names.length; i++){
						var collName = names[i].name.slice(names[i].name.indexOf('.') + 1, names[i].name.lastIndexOf('_'));
						var projNameFromCollName = collName.slice(0, collName.indexOf('_'));
						//check if the collection is for our project
						if(projNameFromCollName == req.params.projectname){
							var revNum = parseInt(names[i].name.slice(names[i].name.lastIndexOf('_') + 1));
							if(revNum >= searchedRevision && revNum < smallestFound){
								smallestFound = revNum;
							}
						}
						
					}
					
					//create the name of the collection searched
					var collectionName = req.params.projectname + "_history_" + smallestFound;
					mongo.db('localhost:8888/genesis_db?auto_reconnect').collection(collectionName).find().toArray(function(err, hist){
						var stringRev = "" + searchedRevision;
						console.log(hist[0][stringRev].deletedFiles);
						if(hist[0][stringRev].deletedFiles != undefined)
							var returnValue = cleaner.cleanFileOp(hist[0][stringRev].deletedFiles[0]);
						else
							var returnValue = 0;
						res.send({
							deletedFiles : returnValue,
						});
					});
				db.close();
				}
			});     
		}else{
			console.log(err)
		}
	});
});

/*
* Route for the AJAX request for the added files 
* in one history for a specified project
*/
app.get('/get_data/:projectname/history/added_files/:rev', function(req, res){
	var searchedRevision = req.params.rev;
	var smallestFound = 9007199254740992; //max integer in javascript
	var nmn_connect = require('mongoose/node_modules/mongodb').connect;
	
	nmn_connect('mongo://localhost:8888/genesis_db?auto_reconnect', function(err, db) {
		if(!err){
			db.collectionNames(function(err, names){ // what I was looking for
				if(!err){
					for(var i = 0; i < names.length; i++){
						var collName = names[i].name.slice(names[i].name.indexOf('.') + 1, names[i].name.lastIndexOf('_'));
						var projNameFromCollName = collName.slice(0, collName.indexOf('_'));
						//check if the collection is for our project
						if(projNameFromCollName == req.params.projectname){
							var revNum = parseInt(names[i].name.slice(names[i].name.lastIndexOf('_') + 1));
							if(revNum >= searchedRevision && revNum < smallestFound){
								smallestFound = revNum;
							}
						}
					}
					
					//create the name of the collection searched
					var collectionName = req.params.projectname + "_history_" + smallestFound;
					mongo.db('localhost:8888/genesis_db?auto_reconnect').collection(collectionName).find().toArray(function(err, hist){
						var stringRev = "" + searchedRevision;
						console.log(hist[0][stringRev].addedFilesCount);
						if(hist[0][stringRev].addedFiles[0] != undefined)
							var returnValue = cleaner.cleanFileOp(hist[0][stringRev].addedFiles[0]);
						else
							var returnValue = 0;
						res.send({
							addedFiles : returnValue,
						});
					});
				db.close();
				}
			});     
		}else{
			console.log(err)
		}
	});
});

/*
* Route for the AJAX request for the modified files 
* in one history for a specified project
*/
app.get('/get_data/:projectname/history/modified_files/:rev', function(req, res){
	var searchedRevision = req.params.rev;
	var smallestFound = 9007199254740992; //max integer in javascript
	var nmn_connect = require('mongoose/node_modules/mongodb').connect;
	
	nmn_connect('mongo://localhost:8888/genesis_db?auto_reconnect', function(err, db) {
		if(!err){
			db.collectionNames(function(err, names){ // what I was looking for
				if(!err){
					for(var i = 0; i < names.length; i++){
						var collName = names[i].name.slice(names[i].name.indexOf('.') + 1, names[i].name.lastIndexOf('_'));
						var projNameFromCollName = collName.slice(0, collName.indexOf('_'));
						//check if the collection is for our project
						if(projNameFromCollName == req.params.projectname){
							var revNum = parseInt(names[i].name.slice(names[i].name.lastIndexOf('_') + 1));
							if(revNum >= searchedRevision && revNum < smallestFound){
								smallestFound = revNum;
							}
						}
					}
					
					//create the name of the collection searched
					var collectionName = req.params.projectname + "_history_" + smallestFound;
					mongo.db('localhost:8888/genesis_db?auto_reconnect').collection(collectionName).find().toArray(function(err, hist){
						var stringRev = "" + searchedRevision;
						console.log(hist[0][stringRev].modifiedFiles);
						if(hist[0][stringRev].modifiedFiles[0] != undefined)
							var returnValue = cleaner.cleanFileOp(hist[0][stringRev].modifiedFiles[0]);
						else
							var returnValue = 0;
						res.send({
							modifiedFiles : returnValue,
						});
					});
				db.close();
				}
			});     
		}else{
			console.log(err)
		}
	});
});

/*
* Route for the AJAX request for handling requests
* for the bug tracker informations
*/
app.get('/get_btinfo/:projectname', function(req, res){
	var projectName = req.params.projectname;
	//waiting for how the btinfo is stored in the DB
});


function getRelation(edges, relationName) {
	return edges[0].edges[relationName];
}

function getAdjList(relation, nodeId) {
	var adjList = null;
	for(var i = 0; i < relation.length; i++) {
		if(relation[i].from == nodeId) {
			adjList = relation[i];
		}
	}
	return adjList;
}

function getNodeList(nodes, adjList) {
	var nodeList = [];
	var i=0;
	if(adjList) {
		// console.log(JSON.stringify(adjList));
		for(var i = 0; i < adjList.to.length; i++) {
			for(var j = 0; j < nodes.length; j++) {
				if(adjList.to[i] == nodes[j].uniqueId) {
					nodeList.push(nodes[j]);
				}
			}
		}
		return nodeList;
	}
	return [];
}

function getSubtreeByRelationName(nodes, edges, relationName, nodeId) {
	relation = getRelation(edges, relationName);
	node = getNodeFromId(nodes, nodeId);
	return getSubtree(nodes, edges, relation, node);
}

function getSubtree(nodes, edges, relation, node) {
	var adjList = getAdjList(relation, node.uniqueId);
	var nodeList = getNodeList(nodes, adjList);
	
	var childNodes = [];
	for(var i = 0; i < nodeList.length; i++) {
		if(nodeList[i] && nodeList[i].properties.name){
			childNodes.push(getSubtree(nodes, edges, relation, nodeList[i]));
		}
	}
	
	if(childNodes.length == 0) 
		return { name: node.properties.name }
	
	return {  name: node.properties.name,  children:  childNodes };
}

var getIdFromName = function(nodes, clazz){
	//find id from name
	console.log(JSON.stringify(nodes));
	for(var i = 0; i < nodes[0].nodes.length; i++){
		if(nodes[0].nodes[i].properties["name"] == clazz && nodes[0].nodes[i].properties["ElementType"] == "Class"){
			return nodes[0].nodes[i].uniqueId;
		}
	}
}

var getNameFromId = function(nodes, uniqueid){
	for(var i = 0; i < nodes[0].nodes.length; i++){
		if(nodes[0].nodes[i].uniqueId == uniqueid){
			return nodes[0].nodes[i].properties["name"];
		}
	}
}

var getNodeFromId = function(nodes, uniqueid){
	for(var i = 0; i < nodes.length; i++){
		if(nodes[i].uniqueId == uniqueid){
			return nodes[i];
		}
	}
}


app.get('/test', function(req, res){
	/*ProjectSchema.find({ProjectName: "ArgoUML", Revision: 300}, function(err, results){
	}); 
	
	ProjectSchema.where('ProjectName', 'ArgoUML').run(function(err, results){
	});
	
	var a = mongoose.connection.db.collection("ArgoUML_rev300_edges", function (err, collection) {
	    collection.find().toArray(function(a,b) { 
		});
	});*/
	mongo.db('localhost:8888/genesis_db?auto_reconnect').collection().ToArray(function (err, colls){
		console.log(JSON.stringify("COLLS : " + colls));
	});
});

/*
* Function to register a new user
*/
function registerUser(request, f){
	console.log("in registerUser");
	mongo.db('localhost:8888/genesis_db?auto_reconnect').collection('users').find({Username: request.body.user.name}).toArray(function(err, users){
		//No user with the same username was found!
		if(users.length == 0){
			console.log("no users with this name and chosen password " + request.body.user.pass);
			var u = {};
			u.Username = request.body.user.name;
			u.Password = request.body.user.pass;
			u.Email = request.body.user.email;
			u.Projects = [];
		
			//save the user
			mongo.db('localhost:8888/genesis_db?auto_reconnect').collection('users').save(u, function(err, users){
				f(true);
			});
		}
		else{
			f(false);
		}
		
	});
}

/*
* Function to login users
*/
function loginUser(username, password, f){
	mongo.db('localhost:8888/genesis_db?auto_reconnect').collection('users').find({Username: username}).toArray(function(err, users){
  		if (err) {
  	 		throw err; 
  		} 
  		else { 
			console.log(users);
			f(users);
  		} 
  	});
}

/**
 * Debugging purposes:
 * Gets the string representation of the specified object. This method is
 * used for debugging
 * @param {Object} Object to convert to string
 * @return {String} The string representation of the object
 */
var toObjectSource = function(obj)   {
   if(obj === null)   {
      return "[null]";
   }
   if(obj === undefined) {
      return "[undefined]";
   }

   var str = "[";
   var member = null;
   for(var each in obj)   {
      try   {
         member = obj[each];
         str += each + "=" + member + ", ";
      }catch(err) {
         alert(err);
      }
   }
   return str + "]";
}
