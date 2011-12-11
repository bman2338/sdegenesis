package ch.usi.inf.genesis.model.core

object FAMIX extends Enumeration {
  type FAMIX = String;
  val NAMESPACE = "Namespace";
  val PACKAGE = "Package";
  val CLASS = "Class";
  val PARAMETRIZABLECLASS = "ParameterizableClass";
  val METHOD = "Method";
  val ATTRIBUTE = "Attribute";
  val INHERITANCEDEFINITION = "Inheritance";
  val INVOCATION = "Invocation";
  val ACCESS = "Access";

  val SIGNATURE = "signature";
  val PARENTTYPE = "parentType";
  val IGNORE_TYPE = "_unknown_";

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


  //PROP are NOT MORE USED  as selection parameters for Navigator
  val RECV_INVOCATIONS_PROP = "invokedBy";
  val SEND_INVOCATIONS_PROP = "invoking";

  //FOR direct access from methods
  val INVOKEDBYMETHODS_PROP = "invokedByMethods";
  val INVOKINGMETHODS_PROP = "invokingMethods";

  val VALUE_PROP = "value";

  val SUBCLASS_PROP = "subclassOf";
  val SUPERCLASS_PROP = "superclassOf";

  val METHODS_PROP = "methods";
  val ATTRIBUTES_PROP = "attributes";
  val CLASSES_PROP = "classes";
  val NAMESPACES_PROP = "namespaces"

  val NAME_PROP = "name"
  val PARENT_PACKAGE = "TODO" //TODO
  val ISSTUB_PROP = "isStub";


  val SOURCE_ANCHOR = "sourceAnchor"
  val SOURCE_FILE_NAME = "fileName"
  val SOURCE_START_LINE = "fileName"
  val SOURCE_END_LINE = "endLine"


  //missing stuff
  val PREVIOUS_ASSIGNEES_PROP = "TODO" //TODO
  val SIGNATURE_PROP = "TODO" //TODO
  val MODIFIERS_PROP = "TODO" //TODO
  val DECLARED_TYPE_PROP = "TODO" //TODO
  val PARENT_TYPE_PROP = "TODO" //TODO
  val BTDEVELOPER_EMAIL_PROP = "TODO" //TODO
  val BUG_DESCRIPTION_PROP = "TODO" //TODO
  val BUG_STATUS_PROP = "TODO" //TODO
  val BUG_ASSIGNEE_PROP = "TODO" //TODO
  val REVISION_COMMENT_PROP = "TODO" //TODO
  val REVISION_DEVELOPER_PROP = "TODO" //TODO
  val REVISION_DATE_PROP = "TODO" //TODO
  val REVISION_PROP = "TODO" //TODO
  val OWNER_PROP = "TODO" //TODO

  val ELEMENTTYPE = "ElementType";
  val METRICS_PROP = "Metrics";

}