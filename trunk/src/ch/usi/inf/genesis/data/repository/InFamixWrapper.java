package ch.usi.inf.genesis.data.repository;
import java.io.File;
import java.io.IOException;


public class InFamixWrapper {

	private final String famixPath;
	private final FamixLanguage language;

	public InFamixWrapper(final FamixLanguage language){
		famixPath = "inFamix";
		this.language = language;
	}

	public InFamixWrapper(final String famixPath, final FamixLanguage language){
		this.famixPath = famixPath;
		this.language = language;
	}

	public File execute(final String sourceFilePath, final String outputFilePath, final boolean asynchronous) throws IOException{
		final File source = new File(sourceFilePath);
		final File output = new File(outputFilePath);
		System.out.println("Source: " + source.getAbsolutePath() + "\nOutput: " + output.getAbsolutePath());
		return this.execute(source, output, asynchronous);
	}

	public File execute(final File sourceFile, final File outputFileName, final boolean asynchronous) throws IOException{
		final Process p = Runtime.getRuntime().exec(String.format("%s -lang %s -path %s -mse %s", famixPath, language.getId(), sourceFile.getAbsolutePath(), outputFileName.getAbsolutePath()));
		if(!asynchronous){
			try {
				p.waitFor();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		return outputFileName;
	}

	public String getFamixPath() {
		return famixPath;
	}

	public FamixLanguage getLanguage() {
		return language;
	}
}
