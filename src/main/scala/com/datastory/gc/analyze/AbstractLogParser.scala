package com.datastory.gc.analyze

import scala.collection.mutable.ListBuffer

/**
  *
  * com.datastory.gc.analyze.LogParser
  *
  *
  * @author lhfcws 
  * @since 2018/1/8
  */
abstract class AbstractLogParser {
  var parsing = false
  var buffer: ListBuffer[String] = ListBuffer[String]()

  def isStartLine(line: String): Boolean = {
    parsing = true
    return true
  }

  protected def isEndLine(line: String): Boolean = {
    return true
  }

  def parse(line: String): ParseResult = {
    buffer += line
    if (isEndLine(line)) {
      return commit()
    } else {
      return null
    }
  }

  protected def commit(): ParseResult = {
    try {
      _parse()
    } finally {
      parsing = false
      buffer.clear()
    }
  }

  protected def _parse(): ParseResult
}

