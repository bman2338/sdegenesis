package ch.usi.genesis.data.repository

import java.io.{BufferedReader, InputStreamReader, InputStream, File}


class InFamixWrapper(infamixPath : String, lang : String) extends ExternalParserWrapper{

  val bashScriptPath = "utils/mse.sh"

  def execute(sourceFilePath: String, outputFile: String, synchronous: Boolean): File = {
                  //script usage: <infamix_path> <infamix_lang> <project_path> <mse_path>"
    val cmd = "./" +bashScriptPath+" "+infamixPath+" "+lang + " " + sourceFilePath + " " + outputFile
    val p: Process = Runtime.getRuntime.exec(cmd)

    var stderr: InputStream = p.getInputStream
    var isr: InputStreamReader = new InputStreamReader(stderr)
    var br: BufferedReader = new BufferedReader(isr)
    var line: String = null

    line = br.readLine
//    while(line != null){
//      println(line)
//      line = br.readLine
//    }
    println("InFamix Done.")

    try {
      println("Waiting...")
      p.waitFor()
    }
    catch {
      case e: InterruptedException => {
        e.printStackTrace()
      }
    }

   new File(outputFile)
  }

  override def execute(sourceFile: File, outputFileName: File, synchronous: Boolean): File = {
    null
  }
}