package ch.usi.inf.genesis.model.core.famix

class AttributeEntity (
			 sourceAnchor: Qualifier, 
			 name: String, 
			 uniqueName: String,
			 //AttributeEntity specifics
			 var belongsTo: ClassEntity = null)  extends Entity(sourceAnchor, name, uniqueName) 
