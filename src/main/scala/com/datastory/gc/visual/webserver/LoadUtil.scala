package com.datastory.gc.visual.webserver

import java.io.{FileInputStream, InputStream}

/**
  *
  * com.datastory.gc.visual.webserver.LoadUtil
  *
  * @author lhfcws 
  * @since 2018/1/22
  */
object LoadUtil {
  val classLoader = {
    var classLoader0 = Thread.currentThread().getContextClassLoader()
    if (classLoader0 == null) {
      classLoader0 = classOf[View].getClassLoader()
    }
    classLoader0
  }

  /**
    * Get an input stream attached to the configuration resource with the
    * given <code>name</code>.
    *
    * @param name configuration resource name.
    * @return an input stream attached to the resource.
    */
  def getConfResourceAsInputStream(name: String): InputStream = try {
    //    val url = classLoader.getResource(name);
    //    if (url == null) {
    //      return null
    //    }
    //    return url.openStream()
    new FileInputStream(name)
  } catch {
    case e: Exception =>
      null
  }

}
