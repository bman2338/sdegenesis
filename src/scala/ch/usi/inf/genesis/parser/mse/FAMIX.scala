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
	
	val BELONGSTOCLASS = "belongsToClass";
	val INVOKEDBY = "invokedBy";
	val CANDIDATES = "candidates";
	val ACCESSES = "accesses";
	val ACCESSEDIN = "accessedIn";
	val SUBCLASS = "subclass";
	val SUPERCLASS = "superclass";
}