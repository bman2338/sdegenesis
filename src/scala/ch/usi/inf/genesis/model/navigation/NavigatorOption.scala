package ch.usi.inf.genesis.model.navigation

object NavigatorOption extends Enumeration {
	type NavigatorOption = Int;
	val CONTINUE  = 0;
	val SKIP_SUBTREE = 1;
	val STOP = 2;
}