package com.datastory.gc.analyze

import com.datastory.gc.analyze.cms.{CMSLogParser, FullGCLogParser, ParNewLogParser}

/**
  *
  * com.datastory.gc.analyze.LogParserFactory
  *
  * @author lhfcws 
  * @since 2018/1/9
  */
class LogParserFactory {
  val cmsParsers = new ParNewLogParser :: new FullGCLogParser :: new CMSLogParser :: Nil

}
