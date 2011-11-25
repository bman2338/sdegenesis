package ch.usi.inf.genesis.model.core

object FAMIX extends Enumeration {
	type FAMIX = String;
	val NAMESPACE = "Namespace";
	val CLASS = "Class";
	val PARAMETRIZABLECLASS = "ParameterizableClass";
	val METHOD = "Method";
	val ATTRIBUTE = "Attribute";
	val INHERITANCEDEFINITION = "Inheritance";
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
	
	//val CONTAINER = "container";
	val CONTAINER = "parentPackage";
	
	val RECV_INVOCATIONS_PROP = "invokedBy";
	val SEND_INVOCATIONS_PROP = "invoking";
	val VALUE_PROP = "value";
	
	val SUBCLASS_PROP = "subclassOf";
	val SUPERCLASS_PROP = "superclassOf";
	
	val METHODS_PROP = "methods";
	val ATTRIBUTES_PROP = "attributes";
	val CLASSES_PROP = "classes";
	val NAMESPACES_PROP = "namespaces"
	
}