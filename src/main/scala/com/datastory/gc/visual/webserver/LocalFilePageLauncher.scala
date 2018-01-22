package com.datastory.gc.visual.webserver

import org.rogach.scallop._

/**
  *
  * com.datastory.gc.visual.webserver.LocalFilePageLauncher
  *
  * @author lhfcws 
  * @since 2018/1/18
  */
class LocalFilePageLauncher(webServerHandler: WebServerHandler, port: Int) extends WebServer(webServerHandler) {
  override def getHttpPort: Int = {
    return port
  }
}

class LocalWebServer extends WebServerHandler {
  override def registerHandlers(): Unit = {
    registerHandler("/localPage", View.createFromResource("localPage.html"))
    registerHandler("/local", new LocalFilepathHandler)
  }
}

object LocalFilePageLauncher {
  class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val port = opt[Int](name = "port", required = true)
    val gcType = opt[String](name = "gcType", required = true)
    val filepath = opt[String](name = "filepath", required = true)
    verify()
  }


  def main(args: Array[String]): Unit = {
    val conf = new Conf(args)

    val port = conf.port()
    val filepath = conf.filepath()
    val gcType = conf.gcType().toLowerCase

    val launcher = new LocalFilePageLauncher(new LocalWebServer, port)

    launcher.startServer()

    print(s"Please visit the url in this host: http://localhost:${port}/localPage?filepath=${filepath}&gcType=${gcType}")

    launcher.join()
  }
}
