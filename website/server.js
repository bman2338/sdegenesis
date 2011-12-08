/*
* Genesis server side
*
* @autor: Genesis Team
*/

//import frameworks
var express = require('express')
  , app = express.createServer(
	express.logger(),
		express.cookieParser(),
		express.session({ secret: 'keyboard cat' })
	)
  , io = require('socket.io').listen(app)
  , mongoose = require('mongoose')
  , schemas = require('./schemas')
  , Schema   = mongoose.Schema
  , jade = require('jade');

var url = require('url')
  , fs = require ('fs');

app.listen(8079);
app.configure(function(){
	app.use(express.bodyParser());
});

var chat = io
  .of('/')
  .on('connection', function (socket) {
    socket.emit('message', {
        that: 'only'
      , '/chat': 'will get'
    });
  });

//Database connections
var db = mongoose.connect('mongodb://localhost:8888/genesis_db2');
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
				name: req.session.username,
				projects: req.session.projects
			};

			//render the management with user connected
			res.render(__dirname + '/pages/management.jade', {
				userInfo: usr,
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
	req.session.authenticated = false;
	req.session.name = undefined;
	req.session.projects = undefined;
	
	//send the user to the index
	res.render(__dirname + '/pages/index.jade', {
		
	});
});

app.post('/addProject', function(req, res){
	var projectName = req.body.project.name;
	var projectrepo = req.body.project.repo;
	var repoType = req.body.project.repoType.toLowerCase();
	var projectBugTracker = req.body.project.bugtracker;
	var bugtrackerType = req.body.project.btType.toLowerCase();
	var projectNameBugtracker = req.body.project.projectnamebugtracker;
		
	//check in the database if there is already there the project
	ProjectSchema.find({ProjectName: projectName}, function(err, projects){
		//no project with this name, we can send it to the scala backend & save it
		if(projects.length == 0){
			var net = require('net');
			var client = net.connect(6969, '172.16.224.88');
			client.write(
				"projectName> "+ projectName + 
				"\n projectRepo> " + projectrepo + 
				"\n repoType> " + repoType + 
				"\n projectBugTracker> " + projectBugTracker + 
				"\n bugTrackerType> " + bugtrackerType + 
				"\n projectBugTrackerName> " + projectNameBugtracker);
			client.end();
			
			//update the user with a brand new project
			UserSchema.update({Username: req.session.name}, {$push: {Projects: projectName}}, {}, function(err){
				
			});
		}
		else {
			//do nothing, this project is already inside!
		}
	});
	
	
	
	var usr = {
		name: req.session.username,
		projects: req.session.projects
	};
	//for now returns management doing nothing
	res.render(__dirname + '/pages/management.jade', {
		userInfo: usr,
	});
});

/*
* Redirect for showing the project
*/
app.get('/show_project/:projectname', function(req, res){
	console.log(req.params.projectname);
	
	var mongo = require('mongoskin');
	mongo.db('localhost:8888/genesis_db').collection('ArgoUML_rev300_edges').find().toArray(function(err, edges){
		mongo.db('localhost:8888/genesis_db').collection('ArgoUML_rev300_nodes').find().toArray(function(err, nodes){

			var newJson = {};
			
			newJson.name = nodes.project;
			newJson.children = [];
						
			var clazz = "BugzillaCrawler";
			var uniqueid = getIdFromName(nodes, clazz);
			
			newJson.children.push(getSubtreeByRelationName(nodes[0].nodes, edges,'attributes', uniqueid));
			
			res.render(__dirname + '/pages/viz.jade', {
				vizJson: newJson,
			});
			
		});
	});
	
	
	var a = mongoose.connection.db.collection("ArgoUML_rev300_edges", function (err, collection) {
		collection.find({}, function(err, results) { 
		});
	});	
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
	ProjectSchema.find({ProjectName: "ArgoUML", Revision: 300}, function(err, results){
	}); 
	
	ProjectSchema.where('ProjectName', 'ArgoUML').run(function(err, results){
	});
	
	var a = mongoose.connection.db.collection("ArgoUML_rev300_edges", function (err, collection) {
	    collection.find().toArray(function(a,b) { 
		});
	});
});

/*
* Function to register a new user
*/
function registerUser(request, f){
	console.log("in registerUser");
	UserSchema.find({Username : request.body.user.name }, function(err, users) { 
		//No user with the same username was found!
		if(users.length == 0){
			console.log("no users with this name and chosen password " + request.body.user.pass);
			var u = new UserSchema();
			u.Username = request.body.user.name;
			u.Password = request.body.user.pass;
			u.Email = request.body.user.email;
			u.Projects = [];
		
			u.save(function(err){
		 		if (err){
					throw err;
				}
			});
			
			f(true);
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
	UserSchema.find({Username: username}, function(err, users) {
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
