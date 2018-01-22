package com.datastory.gc.visual.webserver

import java.lang.management.ManagementFactory
import java.util.HashMap
import java.util.logging.Logger
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.apache.commons.io.IOUtils
import org.eclipse.jetty.server.{Request, Server}
import org.eclipse.jetty.server.handler.AbstractHandler

/**
  *
  * com.datastory.gc.visual.webserver.WebServer
  *
  * @author lhfcws 
  * @since 2018/1/10
  */
class WebServer(webServerHandler: WebServerHandler) {
  val DFT_PORT = 9090
  val LOG = Logger.getLogger(classOf[WebServer].getCanonicalName)
  var server: Server = null

  def getHttpPort: Int = {
    val port = DFT_PORT
    port
  }

  @throws[Exception]
  def startServer(): Server = {
    webServerHandler.registerHandlers()

    val port = getHttpPort
    server = new Server(port)
    LOG.info("=============================================")
    LOG.info("=============================================")
    LOG.info("[SERVER] Open gc analyze server on http port " + port)
    // 配置服务
    server.setHandler(webServerHandler)
    server.start()

    return server
  }

  def join(): Unit = {
    if (server != null) {
      LOG.info(s"[SERVER] Server started. Please check it on webpage like http://localhost:${getHttpPort} ")
      server.join()
      LOG.info("[SERVER] Server stopped.")
    }
  }
}

object WebServer {
  @throws[Exception]
  def main(args: Array[String]): Unit = {
    System.out.println("[PROGRAM] Program started. PID=" + ManagementFactory.getRuntimeMXBean.getName.split("@")(0))
    val server = new WebServer(new WebServerHandler)
    server.startServer()

    server.join()
    System.out.println("[PROGRAM] Program exited.")
  }
}

class WebServerHandler extends AbstractHandler {
  val LOG = Logger.getLogger(classOf[WebServerHandler].getCanonicalName)
  private val handlers: HashMap[String, IRequestHandler] = new HashMap[String, IRequestHandler]

  def registerHandlers(): Unit = {
    registerHandler("/upload", new UploadFileHandler)
  }

  def registerHandler(name: String, iRequestHandler: IRequestHandler): Unit = {
    this.handlers.put(name, iRequestHandler)
  }

  override def handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse): Unit = {
    LOG.info("[REQUEST] URL: " + target)
    request.setCharacterEncoding("utf-8")
    response.setStatus(HttpServletResponse.SC_OK)
    baseRequest.setHandled(true)

    val handler = handlers.get(target)
    if (handler != null) {
      LOG.info(handler.getClass.getCanonicalName)
      response.setContentType("application/json; charset=utf-8")
      handler.handle(request, response)
    } else {
      if (target.endsWith(".js")) {
        response.setContentType("text/javascript; charset=utf-8")
      } else if (target.endsWith(".css")) {
        response.setContentType("text/css; charset=utf-8")
      } else if (target.endsWith(".html")) {
        response.setContentType("text/html; charset=utf-8")
      }

      LOG.info("[UNHANDLE] " + target)
      val resourcePath = target.substring(1)
      val in = LoadUtil.getConfResourceAsInputStream(resourcePath)
      val out = response.getWriter
      if (in == null)
        out.print("<h3>请求失败，请重试！</h3>")
      else {
        out.print(IOUtils.toString(in))
        in.close()
      }
    }
  }
}