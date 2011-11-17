package ch.usi.inf.genesis.parser;

import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.Lexer;

import ch.usi.inf.genesis.parser.java.JavaLexer;
import ch.usi.inf.genesis.parser.java.JavaParser;

/**
 * Class which encapsulates all the language specific parser and lexer pair
 * @author Remo Lemma
 *
 */
public class LanguageFactory {

	/**
	 * Class that wraps a Lexer and a LangaugeParser together
	 * @author Remo Lemma
	 *
	 */
	static class LanguageDefinition {
		
		private final Class<? extends Lexer> lexerClass;
		private final Class<? extends LanguageParser> parserClass;
	
		public LanguageDefinition (final Class<? extends Lexer> lexerClass, final Class<? extends LanguageParser> parserClass) {
			this.lexerClass = lexerClass;
			this.parserClass = parserClass;
		}
		
		public Class<? extends Lexer> getLexerClass () { return this.lexerClass; }
		public Class<? extends LanguageParser> getParserClass () { return this.parserClass; }
		
		public int hashCode () { 
			return (lexerClass.hashCode() + "" + parserClass.hashCode()).hashCode();
		}
		
		public boolean equals (final Object o) {
			return o == null ? false : o.hashCode() == this.hashCode();
		}
	}
	
	private static final Map<String,LanguageDefinition> languages;
	
	static {
		languages = new HashMap<String,LanguageDefinition>();
		languages.put("java", new LanguageDefinition(JavaLexer.class,JavaParser.class));
	}
	
	/**
	 * Get a language definition given the string representation of the language
	 * @param language The language to be searched
	 * @return A LanguageDefinition for the searched language or null if the language is not supported
	 */
	public static LanguageDefinition getLanguage (final String language) {
		return languages.get(language.toLowerCase());
	}
	
}
