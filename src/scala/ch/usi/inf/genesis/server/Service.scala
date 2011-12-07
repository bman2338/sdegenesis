package scala.ch.usi.inf.genesis.server


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket
import collection.mutable.HashMap


object Main {

  class ParseError extends RuntimeException


  def main(args: Array[String]) {
    new Service(6969).run();
  }


}


class Service(val port: Int) {


  def run() {
    try {
      println("Running...");
      val server = new ServerSocket(port);

      while (true) {
        val client = server.accept();
        println("Accepted connection from " + client.getInetAddress)
        val input = client.getInputStream();

        asMap(input) match {
          case None =>
          case Some(map) =>
            actors.Scheduler.execute({
              //TODO run whole download from svn, mse .... etc

              map.foreach(pair => {
                val key = pair._1;
                val value = pair._2;
                println(key + ":" + value)


              })
            });

        }
        client.close();

      }
      server.close();
    }
    catch {
      case ex: Exception => ex.printStackTrace();

    }

    println("Shutting down..")
  }


  def asMap(stream: InputStream): Option[HashMap[String, String]] = {
    val br: BufferedReader = new BufferedReader(new InputStreamReader(stream));
    val sb: StringBuilder = new StringBuilder();
    var line: String = "";
    val map = new HashMap[String, String]();

    while (true) {
      line = br.readLine()
      if (line == null)
        return None;
      val args = line.split(">");
      if (args.length < 2) {
        println("Bad format: " + line);
        return None;
      }

      map.put(args.apply(0).trim(), args.apply(1).trim());
    }
    Some(map);

  }


}