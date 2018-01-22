package com.datastory.gc.analyze.cms

import com.datastory.gc.analyze._
import org.joda.time.DateTime

/**
  *
  * com.datastory.gc.analyze.cms.FullGCLogParser
  *
  * [Log Example]
  * 2017-12-29T16:34:22.062+0800: 502.444: [Full GC2017-12-29T16:34:22.062+0800: 502.444: [CMS: 269706K->259062K(3670016K), 1.1649820 secs] 415139K->259062K(4063232K), [CMS Perm : 46383K->46265K(131072K)], 1.1652950 secs] [Times: user=1.09 sys=0.04, real=1.17 secs]
  *
  * 2017-12-29T16:34:22.062+0800: 502.444: [Full GC2017-12-29T16:34:22.062+0800: 502.444: [CMS: 269706K->259062K(3670016K), 1.1649820 secs] 415139K->259062K(4063232K), [CMS Perm : 46383K->46265K(131072K)], 1.1652950 secs] [Times: user=1.09 sys=0.04, real=1.17 secs]
  *
  * @author lhfcws 
  * @since 2018/1/8
  */
class FullGCLogParser extends AbstractLogParser{
  override def isStartLine(line: String): Boolean = {
    val b = line.contains("[Full GC") && line.contains("[CMS: ")
    return b && super.isStartLine(line)
  }

  override def _parse(): ParseResult = {
    val line = buffer(0).trim
    val parser = new LocalParser(line)

    parser.locatePtrAtEnd("[Full GC")
    val datetime = new DateTime(parser.extractFromPtrUntilLengthIs(28))
    val time = datetime.toDate().getTime()
    val pr = new ParseResult(time)

    // CMS / OldGen
    parser.locatePtrAtEnd("[CMS: ")
    var vint: Int = 0
    var vdouble: Double = 0

    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("OldGen.Before GC", vint * 1.0 / 1024)

    parser.addPtr(3)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("OldGen.After GC", vint * 1.0 / 1024)

    parser.addPtr(2)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("OldGen.allocated", vint * 1.0 / 1024)

    parser.addPtr(4)
    vdouble = parser.extractFromPtrUntilCharIs(' ').toDouble
    pr.put("OldGen.cost", vdouble * 1000)
    pr.put("MajorGC.cost", vdouble * 1000)
    pr.put("FullGC.cost", vdouble * 1000)
    pr.put("PausedGC.cost", vdouble * 1000)

    // Heap
    parser.addPtr(7)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("FullGC.Before GC", vint * 1.0 / 1024)
    pr.put("Heap.Before GC", vint * 1.0 / 1024)

    parser.addPtr(3)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("FullGC.After GC", vint * 1.0 / 1024)
    pr.put("Heap.After GC", vint * 1.0 / 1024)

    parser.addPtr(2)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("FullGC.allocated", vint * 1.0 / 1024)
    pr.put("Heap.allocated", vint * 1.0 / 1024)

    // Perm
    parser.locatePtrAtEnd("[CMS Perm : ")

    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("Perm.Before GC", vint * 1.0 / 1024)

    parser.addPtr(3)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("Perm.After GC", vint * 1.0 / 1024)

    parser.addPtr(2)
    vint = parser.extractFromPtrUntilCharIs('K').toInt
    pr.put("Perm.allocated", vint * 1.0 / 1024)

    parser.addPtr(5)
    vdouble = parser.extractFromPtrUntilCharIs(' ').toDouble
    pr.put("Heap.cost", vdouble * 1000)

    return pr
  }
}
