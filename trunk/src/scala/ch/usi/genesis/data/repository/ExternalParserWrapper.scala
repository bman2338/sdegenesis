package ch.usi.genesis.data.repository

import java.io.File


trait ExternalParserWrapper {
  def execute(sourceFilePath: String, outputFilePath: String, synchronous: Boolean): File

  def execute(sourceFile: File, outputFileName: File, synchronous: Boolean): File
}