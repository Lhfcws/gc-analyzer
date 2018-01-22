package com.datastory.gc.visual.webserver

import com.datastory.gc.analyze.{ParseResult, Util}

import scala.collection.mutable

/**
  * CMS的json渲染
  *
  * com.datastory.gc.visual.webserver.CMSVisualRenderer
  *
  * @author lhfcws 
  * @since 2018/1/15
  */
class CMSVisualRenderer(name: String, analyzRes: Seq[ParseResult]) extends VisualRenderer(name, analyzRes) {
  override def render(): Map[String, Any] = {
    var result = mutable.HashMap[String, Any]()
    result ++= Map("_meta" -> Map("gctype" -> "CMS"))

    // Summary
    val startTime = analyzRes.head.time
    val endTime = analyzRes.last.time
    result ++= Map("Summary" -> Map("filename" -> name, "duration" -> (endTime - startTime)))

    // JVM Heap
    val (yg, _1) = findFirstExists(analyzRes.reverseIterator, YG + ".allocated")
    val (og, _2) = findFirstExists(analyzRes.reverseIterator, OG + ".allocated")
    val (ygPeak, _3) = findMax(analyzRes.iterator, YG + ".allocated")
    val (ogPeak, _4) = findMax(analyzRes.iterator, OG + ".allocated")
    val heapSummary = Map("allocated" -> Map(YG -> yg, OG -> og), "peak" -> Map(YG -> ygPeak, OG -> ogPeak))
    result ++= Map("JVM Heap" -> Map(
      "_comments" -> "Default heap measure is MB, time measure is MS.",
      "summary" -> heapSummary,
      YG -> getSeries(Map(
        YG + ".allocated" -> "allocated",
        YG + ".Before GC" -> "Before GC",
        YG + ".After GC" -> "After GC"
      )),
      OG -> getSeries(Map(
        OG + ".allocated" -> "allocated",
        OG + ".Before GC" -> "Before GC",
        OG + ".After GC" -> "After GC"
      )),
      HEAP -> getSeries(Map(
        HEAP + ".allocated" -> "allocated",
        HEAP + ".Before GC" -> "Before GC",
        HEAP + ".After GC" -> "After GC"
      ))
    ))

    // GC Stats
    val gcDuration = Map(
      YG -> getDiffSeries(Map((YG + ".Before GC", YG + ".After GC") -> "mb")),
      OG -> getDiffSeries(Map((OG + ".Before GC", OG + ".After GC") -> "mb"))
    )

    val minorGCStats = new GcStats().applyList(analyzRes, new Function[ParseResult, Option[Double]] {
      def apply(pr: ParseResult): Option[Double] = {
        if (pr.params.contains("MinorGC.cost")) {
          return Some(pr.params("MinorGC.cost").asInstanceOf[Double])
        }

        return null
      }
    }).toMap()

    val majorGCStats = new GcStats().applyList(analyzRes, new Function[ParseResult, Option[Double]] {
      def apply(pr: ParseResult): Option[Double] = {
        if (pr.params.contains("MajorGC.cost")) {
          return Some(pr.params("MajorGC.cost").asInstanceOf[Double])
        }

        return null
      }
    }).toMap()

    val fullGCStats = new GcStats().applyList(analyzRes, new Function[ParseResult, Option[Double]] {
      def apply(pr: ParseResult): Option[Double] = {
        if (pr.params.contains(FGC + ".cost")) {
          return Some(pr.params(FGC + ".cost").asInstanceOf[Double])
        }

        return null
      }
    }).toMap()

    val pausedGCStats = new GcStats().applyList(analyzRes, new Function[ParseResult, Option[Double]] {
      def apply(pr: ParseResult): Option[Double] = {
        if (pr.params.contains("PausedGC.cost")) {
          return Some(pr.params("PausedGC.cost").asInstanceOf[Double])
        }

        return null
      }
    }).toMap()

    val totalGCStats = new GcStats().applyList(analyzRes, new Function[ParseResult, Option[Double]] {
      def apply(pr: ParseResult): Option[Double] = {
        if (pr.params.contains("MinorGC.cost")) {
          return Some(pr.params("MinorGC.cost").asInstanceOf[Double])
        } else if (pr.params.contains("MajorGC.cost")) {
          return Some(pr.params("MajorGC.cost").asInstanceOf[Double])
        }

        return null
      }
    }).toMap()

    result ++= Map("GC Stats" -> Map(
      "GC Duration" -> gcDuration,
      "Minor GC Stats" -> minorGCStats,
      "Major GC Stats" -> majorGCStats,
      "Full GC Stats" -> fullGCStats,
      "Paused GC Stats" -> pausedGCStats,
      "Total GC Stats" -> totalGCStats
    ))

    // CMS Stats
    val cmsStats = new CMSStats().applyList(analyzRes)
    if (!cmsStats.isEmpty()) {
      result ++= Map[String, Any]("CMS Stats" -> Map[String, Any](
        "_comments" -> "Only Final Remark & Initial Mark will cause STW in CMS.",
        "Total Time" -> cmsStats.toTotalMap(),
        "Avg Time" -> cmsStats.toAvgMap(),
        "CMS Total Pause Time" -> cmsStats.toPauseTotalMap(),
        "CMS Avg Pause Time" -> cmsStats.toPauseAvgMap()
      ))
    }

    // -- return
    return result.toMap
  }
}

class CMSStats {
  var ygcCount = 0
  var ygcTime = 0.0

  var cmsCount = 0
  var cmsTime = 0.0

  var sweepTime = 0.0
  var abrtPrecleanTime = 0.0
  var markTime = 0.0
  var finalRemarkTime = 0.0
  var precleanTime = 0.0
  var resetTime = 0.0
  var initMarkTime = 0.0

  var pausedCount = 0
  var pausedTime = 0.0

  def applyList(prs: Seq[ParseResult]): CMSStats = {
    for (pr <- prs) {
      apply(pr)
    }
    return this
  }

  def apply(pr: ParseResult): Unit = {
    if (pr.contains("CMS.cost")) {
      cmsCount += 1
      val d = pr.getDouble("CMS.cost").get
      cmsTime += d

      var key: String = null

      key = "Concurrent Sweep.cost"
      if (pr.contains(key)) {
        val d1 = pr.getDouble(key).get
        sweepTime += d1
      }

      key = "Concurrent Abortable Preclean.cost"
      if (pr.contains(key)) {
        val d1 = pr.getDouble(key).get
        abrtPrecleanTime += d1
      }

      key = "Concurrent Mark.cost"
      if (pr.contains(key)) {
        val d1 = pr.getDouble(key).get
        markTime += d1
      }

      key = "Final Remark.cost"
      if (pr.contains(key)) {
        val d1 = pr.getDouble(key).get
        finalRemarkTime += d1
      }

      key = "Concurrent Preclean.cost"
      if (pr.contains(key)) {
        val d1 = pr.getDouble(key).get
        precleanTime += d1
      }

      key = "Concurrent Reset.cost"
      if (pr.contains(key)) {
        val d1 = pr.getDouble(key).get
        resetTime += d1
      }

      key = "Initial Mark.cost"
      if (pr.contains(key)) {
        val d1 = pr.getDouble(key).get
        initMarkTime += d1
      }

    } else if (pr.contains("MinorGC.cost")) {
      val d = pr.getDouble("MinorGC.cost").get
      ygcTime += d
      ygcCount += 1
    } else if (pr.contains("PausedrGC.cost")) {
      val d = pr.getDouble("PausedrGC.cost").get
      pausedTime += d
      pausedCount += 1
    }
  }

  def toTotalMap(): Map[String, Any] = {
    return Map[String, Any](
      "Young GC" -> Util.precision2(ygcTime),
      "Concurrent Sweep" -> Util.precision2(sweepTime),
      "Concurrent Abortable Preclean" -> Util.precision2(abrtPrecleanTime),
      "Concurrent Mark" -> Util.precision2(markTime),
      "Final Remark" -> Util.precision2(finalRemarkTime),
      "Concurrent Preclean" -> Util.precision2(precleanTime),
      "Concurrent Reset" -> Util.precision2(resetTime),
      "Initial Mark" -> Util.precision2(initMarkTime)
    )
  }

  def toAvgMap(): Map[String, Any] = {
    val c = if (cmsCount == 0) 1 else cmsCount
    val y = if (ygcCount == 0) 1 else ygcCount

    return Map[String, Any](
      "Young GC" -> Util.precision2(ygcTime / y),
      "Concurrent Sweep" -> Util.precision2(sweepTime / c),
      "Concurrent Abortable Preclean" -> Util.precision2(abrtPrecleanTime / c),
      "Concurrent Mark" -> Util.precision2(markTime / c),
      "Final Remark" -> Util.precision2(finalRemarkTime / c),
      "Concurrent Preclean" -> Util.precision2(precleanTime / c),
      "Concurrent Reset" -> Util.precision2(resetTime / c),
      "Initial Mark" -> Util.precision2(initMarkTime / c)
    )
  }

  def toPauseTotalMap(): Map[String, Any] = {
    val concurrentTime = cmsTime - pausedTime
    return Map[String, Any](
      "pause" -> Util.precision2(pausedTime),
      "concurrent" -> Util.precision2(concurrentTime)
    )
  }

  def toPauseAvgMap(): Map[String, Any] = {
    val concurrentTime = cmsTime - pausedTime
    val concurrentCount = cmsCount - pausedCount

    val c = if (concurrentCount == 0) 1 else concurrentCount
    val p = if (pausedCount == 0) 1 else pausedCount

    return Map[String, Any](
      "pause" -> Util.precision2(pausedTime / p) ,
      "concurrent" -> Util.precision2(concurrentTime / c)
    )
  }

  def isEmpty(): Boolean = {
    return ygcCount == 0 && cmsCount == 0 && pausedCount == 0
  }
}
