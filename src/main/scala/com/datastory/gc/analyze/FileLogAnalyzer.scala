package com.datastory.gc.analyze

import java.io._

import com.datastory.gc.analyze.cms.{CMSLogParser, FullGCLogParser, ParNewLogParser}

import scala.collection.mutable.ListBuffer


/**
  *
  * com.datastory.gc.analyze.FileLogAnalyzer
  *
  * @author lhfcws 
  * @since 2018/1/9
  */
class FileLogAnalyzer(in: InputStream, gcType: String) {

  def this(localfile: String, gcType0: String) {
    this(new FileInputStream(localfile), gcType0)
  }

  val parsers = new LogParserFactory().cmsParsers

  def analyze(): Seq[ParseResult] = {
    val isReader = new InputStreamReader(in)
    val br = new BufferedReader(isReader)

    try {
      val ret = new ListBuffer[ParseResult]
      var line: String = null
      var flag = true
      while (flag) {
        line = br.readLine()
        flag = line != null
        if (flag)
          for (parser <- parsers) {
            if (parser.isStartLine(line)) {
              val pr = parser.parse(line)
              if (pr != null) {
                ret += pr
              }
            }
          }
      }

      ret
    } finally {
      br.close()
      isReader.close()
    }
  }
}
