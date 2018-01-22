package com.datastory.gc.analyze.cms


import com.datastory.gc.analyze._
import org.joda.time.DateTime


/**
  *
  * com.datastory.gc.analyze.cms.CMSLogParser
  *
  * @see <a href="https://blogs.oracle.com/poonam/understanding-cms-gc-logs">Ref</a>
  *
  *      [Log Example]
  *      2016-11-28T16:42:17.055+0800: 9597.558: [GC [1 CMS-initial-mark: 10261739K(20447232K)] 10303820K(20919104K), 0.0055220 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
  *      2016-11-28T16:42:17.060+0800: 9597.563: [CMS-concurrent-mark-start]
  *      2016-11-28T16:42:17.278+0800: 9597.781: [CMS-concurrent-mark: 0.218/0.218 secs] [Times: user=2.68 sys=0.17, real=0.22 secs]
  *      2016-11-28T16:42:17.278+0800: 9597.781: [CMS-concurrent-preclean-start]
  *      2016-11-28T16:42:17.327+0800: 9597.830: [CMS-concurrent-preclean: 0.048/0.049 secs] [Times: user=0.14 sys=0.00, real=0.05 secs]
  *      2016-11-28T16:42:17.327+0800: 9597.830: [CMS-concurrent-abortable-preclean-start]
  *      2016-11-28T16:42:20.696+0800: 9601.198: [CMS-concurrent-abortable-preclean: 3.129/3.369 secs] [Times: user=5.18 sys=0.23, real=3.37 secs]
  *      2016-11-28T16:42:20.697+0800: 9601.199: [GC[YG occupancy: 313664 K (471872 K)]2016-11-28T16:42:20.697+0800: 9601.200: [Rescan (parallel) , 0.0394850 secs]2016-11-28T16:42:20.736+0800: 9601.239: [weak refs processing, 0.0086650 secs]2016-11-28T16:42:20.745+0800: 9601.248: [scrub string table, 0.0009460 secs] [1 CMS-remark: 10317928K(20447232K)] 10631593K(20919104K), 0.0503540 secs] [Times: user=1.41 sys=0.01, real=0.05 secs]
  *      2016-11-28T16:42:20.747+0800: 9601.250: [CMS-concurrent-sweep-start]
  *
  *
  * @author lhfcws 
  * @since 2018/1/8
  */
class CMSLogParser extends AbstractLogParser {
  val parsers = new InitialMarkLogParser() ::
      new CMSSimpleLogParser("[CMS-concurrent-mark: ", "Concurrent Mark") ::
      new CMSSimpleLogParser("[CMS-concurrent-preclean: ", "Concurrent Preclean") ::
      new CMSSimpleLogParser("[CMS-concurrent-abortable-preclean: ", "Concurrent Abortable Preclean") ::
      new CMSSimpleLogParser("[CMS-concurrent-sweep: ", "Concurrent Sweep") ::
      new CMSSimpleLogParser("[CMS-concurrent-reset: ", "Concurrent Reset") ::
      new RemarkLogParser() ::
      Nil

  override def isStartLine(line: String): Boolean = {
    val b = line.contains("CMS-initial-mark")
    return b && super.isStartLine(line)
  }

  override protected def isEndLine(line: String): Boolean = {
    return line.contains("[CMS-concurrent-sweep-start]")
  }

  override def _parse(): ParseResult = {
    val parseResult = new ParseResult(-1)

    for (line <- buffer) {
      for (parser <- this.parsers) {
        if (parser.isStartLine(line)) {
          val pr = parser.parse(line)
          if (pr != null) {
            parseResult.merge(pr)
          }
        }
      }
    }

    return parseResult
  }

  def putOrAdd(pr: ParseResult, key:String, d: Double) = {
    if (pr.params.contains(key)) {
      val v = pr.params(key).asInstanceOf[Double]
      pr.params.put(key, v + d)
    } else {
      pr.params.put(key, d)
    }
  }

  class InitialMarkLogParser extends AbstractLogParser {
    var time: Long = 0

    def setTime(t: Int) = {
      this.time = t
    }

    override def isStartLine(line: String): Boolean = {
      val b = line.contains("CMS-initial-mark") && !line.contains("-start")
      return b && super.isStartLine(line)
    }

    override def _parse(): ParseResult = {
      // [1 CMS-initial-mark:
      var vint: Int = 0
      var vdouble: Double = 0
      val parser = new LocalParser(buffer(0).trim)

      val datetimeStr = parser.extractFromPtrUntilLengthIs(28)
      val datetime = new DateTime(datetimeStr)
      this.time = datetime.toDate().getTime()

      val pr = new ParseResult(this.time)

      parser.locatePtrAtEnd("[1 CMS-initial-mark: ")
      vint = parser.extractFromPtrUntilCharIs('K').toInt
      pr.put("Initial Mark.Used OldGen", Util.kbToMb(vint))

      parser.addPtr(2)
      vint = parser.extractFromPtrUntilCharIs('K').toInt
      pr.put("Initial Mark.OldGen", Util.kbToMb(vint))

      parser.addPtr(4)
      vint = parser.extractFromPtrUntilCharIs('K').toInt
      pr.put("Initial Mark.Used Heap", Util.kbToMb(vint))

      parser.addPtr(2)
      vint = parser.extractFromPtrUntilCharIs('K').toInt
      pr.put("Initial Mark.Heap", Util.kbToMb(vint))

      parser.addPtr(3)
      vdouble = parser.extractFromPtrUntilCharIs(' ').toDouble
      pr.put("Initial Mark.cost", Util.secToMs(vdouble))
      putOrAdd(pr, "PausedGC.cost", Util.secToMs(vdouble))
      putOrAdd(pr, "CMS.cost", Util.secToMs(vdouble))
      putOrAdd(pr, "MajorGC.cost", Util.secToMs(vdouble))

      return pr
    }
  }

  class RemarkLogParser extends AbstractLogParser {
    var time: Int = 0

    def setTime(t: Int) = {
      this.time = t
    }

    override def isStartLine(line: String): Boolean = {
      val b = line.contains(" [1 CMS-remark:") && !line.contains("-start")
      return b && super.isStartLine(line)
    }

    override def _parse(): ParseResult = {
      // [1 CMS-remark:
      var vint: Int = 0
      var vdouble: Double = 0
      val parser = new LocalParser(buffer(0).trim)
      val pr = new ParseResult(this.time)

      parser.locatePtrAtEnd("[1 CMS-remark: ")
      vint = parser.extractFromPtrUntilCharIs('K').toInt
      pr.put("Final Remark.Used OldGen", Util.kbToMb(vint))

      parser.addPtr(2)
      vint = parser.extractFromPtrUntilCharIs('K').toInt
      pr.put("Final Remark.OldGen", Util.kbToMb(vint))

      parser.addPtr(4)
      vint = parser.extractFromPtrUntilCharIs('K').toInt
      pr.put("Final Remark.Used Heap", Util.kbToMb(vint))

      parser.addPtr(2)
      vint = parser.extractFromPtrUntilCharIs('K').toInt
      pr.put("Final Remark.Heap", Util.kbToMb(vint))

      parser.addPtr(3)
      vdouble = parser.extractFromPtrUntilCharIs(' ').toDouble
      pr.put("Final Remark.cost", Util.secToMs(vdouble))
      putOrAdd(pr, "PausedGC.cost", Util.secToMs(vdouble))
      putOrAdd(pr, "CMS.cost", Util.secToMs(vdouble))
      putOrAdd(pr, "MajorGC.cost", Util.secToMs(vdouble))

      return pr
    }
  }

  class CMSSimpleLogParser(prefix: String, name: String) extends AbstractLogParser {
    var time: Int = 0

    def setTime(t: Int) = {
      this.time = t
    }

    override def isStartLine(line: String): Boolean = {
      val b = line.contains(this.prefix) && !line.contains("-start")
      return b && super.isStartLine(line)
    }

    override def _parse(): ParseResult = {
      val pr = new ParseResult(time)
      val parser = new LocalParser(buffer(0).trim)
      val vdouble = parser.extractFromPtrUntilCharIs('/').toDouble
      pr.put(name + ".cost", Util.secToMs(vdouble))
      putOrAdd(pr, "CMS.cost", Util.secToMs(vdouble))
      putOrAdd(pr, "MajorGC.cost", Util.secToMs(vdouble))

      return pr
    }
  }

}
