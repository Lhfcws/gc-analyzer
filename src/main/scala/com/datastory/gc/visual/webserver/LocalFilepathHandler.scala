package com.datastory.gc.visual.webserver

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.datastory.gc.analyze.{FileLogAnalyzer, ParseResult}

/**
  *
  * com.datastory.gc.visual.webserver.LocalFilepathHandler
  *
  * @author lhfcws 
  * @since 2018/1/10
  */
class LocalFilepathHandler extends IRequestHandler {
  override def handle(request: HttpServletRequest, response: HttpServletResponse): Unit = {
    val filepath = request.getParameter("filepath")
    var gcType = request.getParameter("gcType")
    if (gcType == null)
      gcType = "cms"

    val resJson = _handle(gcType, filepath)

    val writer = response.getWriter
    try {
      writer.print(resJson)
      writer.flush()
    } finally {
      writer.close()
    }
  }

  def _handle(gcType: String, filepath: String): String = {
    val analyzeRes = new FileLogAnalyzer(filepath, gcType).analyze()
    val arr = filepath.split("/")
    val fname = arr(arr.length - 1)

    val visualRenderer = VisualRenderer.createVisualRenderer(gcType, analyzeRes, fname)
    val rendered = visualRenderer.render()
    return JsonUtil.dumpJson(rendered)
  }
}

