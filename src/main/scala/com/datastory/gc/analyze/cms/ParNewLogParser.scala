package com.datastory.gc.analyze.cms

import com.datastory.gc.analyze.{AbstractLogParser, LocalParser, ParseResult}
import org.joda.time.DateTime

/**
  *
  * com.datastory.gc.analyze.cms.ParNewLogParser
  *
  * [Log Example]
  * 2017-12-29T16:33:14.990+0800: 435.372: [GC2017-12-29T16:33:14.991+0800: 435.373: [ParNew: 299358K->25785K(393216K), 0.0498260 secs] 551185K->288787K(4063232K), 0.0510470 secs] [Times: user=0.18 sys=0.01, real=0.05 secs]
  *
  * 2017-12-29T16:33:14.990+0800: 435.372: [GC${yyyy-MM-ddTHH:mm:ss.ms}+0800: 435.373: [ParNew: ${YoungGen.Before GC}K->${YoungGen.After GC}K(${YoungGen}K), ${YoungGen.cost} secs] ${Heap.Before GC}K->${Heap.After GC}K(${Heap}K), ${Heap.cost} secs] [Times: user=0.18 sys=0.01, real=0.05 secs]
  *
  * @author lhfcws 
  * @since 2018/1/8
  */
class ParNewLogParser extends AbstractLogParser {
  override def isStartLine(line: String): Boolean = {
    val b = line.contains("[GC") && line.contains("[ParNew: ")
    return b && super.isStartLine(line)
  }

  override def _parse(): ParseResult = {
    val line = buffer(0).trim
    val parser = new LocalParser(line)

    parser.locatePtrAtEnd("[GC")
    val datetimeStr = parser.extractFromPtrUntilLengthIs(28)
    val datetime = new DateTime(datetimeStr)
    val time = datetime.toDate().getTime()
    val pr = new ParseResult(time)


    // YoungGen
    parser.locatePtrAtEnd("[ParNew: ")
    var vint: Int = 0
    var vdouble: Double = 0

    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("YoungGen.Before GC", vint * 1.0 / 1024)

    parser.addPtr(3)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("YoungGen.After GC", vint * 1.0 / 1024)

    parser.addPtr(2)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("YoungGen.allocated", vint * 1.0 / 1024)

    parser.addPtr(4)
    vdouble = parser.extractFromPtrUntilCharIs(' ').toDouble
    pr.put("YoungGen.cost", vdouble * 1000)
    pr.put("MinorGC.cost", vdouble * 1000)

    // Heap
    parser.addPtr(7)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("Heap.Before GC", vint * 1.0 / 1024)

    parser.addPtr(3)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("Heap.After GC", vint * 1.0 / 1024)

    parser.addPtr(2)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("Heap.allocated", vint * 1.0 / 1024)

    parser.addPtr(4)
    vdouble = parser.extractFromPtrUntilCharIs(' ').toDouble
    pr.put("Heap.cost", vdouble * 1000)

    return pr
  }
}
