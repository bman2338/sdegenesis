/*
* This file contains all the schemas that are
* in the database.
*
* @author: Genesis Team
*/

var mongoose = require('mongoose'),
    Schema   = mongoose.Schema;

var Property = new Schema({
	Key: String,
});

var Node = new Schema({
	UniqueId: String,
	Properties: [Property],
	Metrics: [Property],
});

var Relation = new Schema({
	From: Number,
	To: [Number],
});

var EdgesSet = new Schema({
	RelationName: String,
	Relations: [Relation]
});

var ProjectSchema = new Schema({
	ProjectName: {type: String, index: true},
	Revision: Number,
	Nodes: [Node],
	Edges: [EdgesSet],
});

var UserSchema = new Schema({
	Username: {type: String, index: true},
	Password: String,
	Email: String,
	Projects: [ProjectSchema],
});

mongoose.model('User', UserSchema);
mongoose.model('Project', ProjectSchema);
mongoose.model('Node', Node);
mongoose.model('EdgesSet', EdgesSet);
mongoose.model('Property', Property);
mongoose.model('Relation', Relation);