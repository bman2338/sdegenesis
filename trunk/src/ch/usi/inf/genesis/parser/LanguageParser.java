package ch.usi.inf.genesis.parser;

import java.io.File;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;

import ch.usi.inf.genesis.parser.LanguageFactory.LanguageDefinition;

/**
 * Generic Language Parser. All Parsers in Generis inherit from this.
 * You should generate the AST calling the static generateTree() method.
 * @author Remo Lemma
 *
 */
public abstract class LanguageParser extends Parser {

	private Tree tree;
	
	public LanguageParser(TokenStream input) {
		super(input);
	}
	
    public LanguageParser(TokenStream input, RecognizerSharedState state) {
    	super(input,state);
    }
    
    public static LanguageParser generateTreeParser (final String fileName) throws Exception {
    	final CharStream input = new ANTLRFileStream(new File(fileName).getAbsolutePath());
    	// TODO: Extract Langauge from fileName
    	final LanguageDefinition language = LanguageFactory.getLanguage("java");
    	
    	final Lexer lexer = language.getLexerClass().getConstructor(CharStream.class).newInstance(input);
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
    	final LanguageParser parser = language.getParserClass().getConstructor(TokenStream.class).newInstance(tokens);
    	parser.tree = parser.getInternalTree();
    	//System.out.println(fileName);
    	return parser;
    }
        
    public void walkTree () throws Exception {
    	final Tree tree = getTree();
    	System.out.println(tree.toStringTree());
    	/*
        CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
    	//System.out.println(tree.getChildCount());
    	final TreeAdaptor adaptor = this.getTreeAdaptor();
    	final TreeVisitor visitor = new TreeVisitor(adaptor);
    	visitor.visit(tree,new TreeVisitorAction() {

			public Object post (final Object tree) {
				if (((CommonTree)tree).getType() == 23)
					System.out.println(((CommonTree)tree).getChildCount());
				return tree;
			}

			public Object pre (final Object tree) {
				return tree;
			}
    		
    	});*/
    }
    
    public Tree getTree () {
    	return tree;
    }
            
    protected abstract TreeAdaptor getTreeAdaptor ();
    
    protected abstract Tree getInternalTree () throws Exception;
}
