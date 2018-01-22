package com.datastory.gc.visual.webserver


import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.apache.commons.io.IOUtils

/**
  *
  * com.datastory.gc.visual.webserver.View
  *
  * @author lhfcws 
  * @since 2018/1/22
  */
class View(html: String) extends IRequestHandler {
  override def handle(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    print("[DEBUG] view : " + request.getContextPath)

    response.setContentType("text/html; charset=utf-8")
    val writer = response.getWriter
    writer.print(html)
    writer.flush()
    writer.close()
  }
}

object View {
  val ROOT = "web/"

  def createFromResource(htmlPath: String): View = {
    print("[DEBUG] create View: " + htmlPath)
    val in = LoadUtil.getConfResourceAsInputStream(ROOT + htmlPath)
    if (in == null)
      throw new RuntimeException("Invalid resource path : " + ROOT + htmlPath)

    val htmlString = IOUtils.toString(in)
    in.close()
    return new View(htmlString)
  }


}
