package ch.usi.inf.genesis.utils;

import ch.usi.inf.genesis.parser.LanguageParser;
import org.antlr.runtime.tree.Tree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Stack;

public class JavaASTGenerator {
    public static void main(final String[] args) throws Exception {
        final File prjFolder = new File(args[0]);
        File outFolder = null;
        boolean out = false;
        if (args.length > 1) {
            outFolder = new File(args[1]);
            out = true;
        }

        final Stack<File> toVisit = new Stack<File>();
        toVisit.push(prjFolder);

        final Date start = new Date();
        int count = 0;
        int dirCount = 0;
        int astCount = 0;
        int errCount = 0;
        while (!toVisit.isEmpty()) {
            final File dir = toVisit.pop();

            for (File f : dir.listFiles()) {
                if (f.isHidden())
                    continue;

                if (f.isDirectory()) {

                    toVisit.push(f);
                    ++dirCount;
                    continue;
                }
                if (!f.getName().matches("[\\s|\\S].*.java")) continue;

                ++count;
                //System.out.println("Parsing " + f.getCanonicalPath());
                /*final CharStream input = new ANTLRFileStream(f.getAbsolutePath());
                    JavaLexer lex = new JavaLexer(input);
                    CommonTokenStream tokens = new CommonTokenStream(lex);
                    JavaParser parser = new JavaParser(tokens);*/

                final LanguageParser parser = LanguageParser.generateTreeParser(f.getAbsolutePath());
                final Tree anAST = parser.getTree();
                parser.walkTree();

                if (out) {

                    if (anAST == null) {
                        ++errCount;
                        continue;
                    }

                    final String treeStr = anAST.toStringTree();
                    if (treeStr.length() == 0) {
                        ++errCount;
                        continue;
                    }

                    final FileWriter fstreamOut = new FileWriter(new File(outFolder.getAbsolutePath() + "/" + astCount + "-" + f.getName() + ".txt"));
                    final BufferedWriter fileWriter = new BufferedWriter(fstreamOut);
                    fileWriter.write(treeStr);
                    fileWriter.flush();
                    fstreamOut.close();
                    ++astCount;
                }
            }
        }

        final Date end = new Date();
        @SuppressWarnings("deprecation")
        final int totalTime = end.getSeconds() - start.getSeconds();
        System.out.println("Process Start: " + start);
        System.out.println("Process End: " + end);
        System.out.println("Total Time: " + totalTime + "s");
        System.out.println("Total Files Parsed: " + count);
        System.out.println("Total Directories: " + (dirCount + 1));
        System.out.println("Total AST Written: " + astCount);
        System.out.println("Total Errors: " + errCount);
        System.out.println("AST Elaboration Done.");
    }
}