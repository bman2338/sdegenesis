package ch.usi.inf.genesis.parser.mse

object FAMIX extends Enumeration {
	type FAMIX = String;
	val NAMESPACE = "Namespace";
	val CLASS = "Class";
	val METHOD = "Method";
	val ATTRIBUTE = "Attribute";
	val INHERITANCEDEFINITION = "InheritanceDefinition";
	val INVOCATION = "Invocation";
	val ACCESS = "Access";
	

	val PARENTTYPE = "parentType";
	
	val SENDER = "sender";
	val RECEIVER = "receiver";
	val PREVIOUS = "previous";
	val CANDIDATES = "candidates";
	val PARENTBEHAVIORALENTITY = "parentBehaviouralEntity";
	
	val ACCESSES = "accesses";
	val ACCESSEDIN = "accessedIn";
	
	val SUBCLASS = "subclass";
	val SUPERCLASS = "superclass";
	
	
	//FAMIX 2.0 to FAMIX 3.0
//	val BELONGSTOCLASS = "belongsToClass";
//	val INVOKEDBY = "invokedBy";
}