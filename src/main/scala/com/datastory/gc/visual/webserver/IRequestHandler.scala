package com.datastory.gc.visual.webserver

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

/**
  * Jetty的handler接口类
  * com.datastory.gc.visual.webserver.IRequestHandler
  *
  * @author lhfcws 
  * @since 2018/1/10
  */
trait IRequestHandler {
  def handle(request: HttpServletRequest, response: HttpServletResponse): Unit
}
