package ch.usi.inf.genesis.data.repository;

import java.io.File;
import java.io.IOException;

public interface IExternalModelParserWrapper {
	public File execute(final String sourceFilePath, final String outputFilePath, final boolean asynchronous) throws IOException;
	public File execute(final File sourceFile, final File outputFileName, final boolean asynchronous) throws IOException;
}
