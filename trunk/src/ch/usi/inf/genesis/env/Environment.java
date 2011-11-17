package ch.usi.inf.genesis.env;

import java.util.HashMap;

/**
 * @author patrick
 * The environment contains all the definitions of a certain
 * scope. Whenever a variable is found it can be search in its scope.
 * For instance:a variable in a method can be scoped to the method itself
 * or the class (possible specialization can be made for class and methods)
 */
public class Environment {
	private HashMap< String, Declaration > definitions;
	
	public void addDefinition(Declaration def)
	{
		//TODO all manipulations
		definitions.put(def.getName(), def);
	}
	
	public Declaration getDefinition(String name) 
	{
		return definitions.get(name);
	}
}
