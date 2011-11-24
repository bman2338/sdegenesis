package ch.usi.inf.genesis.model.core.famix

class AttributeEntity (
			 name: String, 
			 //AttributeEntity specifics
			 var belongsTo: ClassEntity = null)  extends Entity(name) 
