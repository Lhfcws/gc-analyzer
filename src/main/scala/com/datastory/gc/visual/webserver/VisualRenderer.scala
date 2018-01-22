package com.datastory.gc.visual.webserver

import com.datastory.gc.analyze.{ParseResult, Util}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  *
  * com.datastory.gc.visual.webserver.VisualRenderer
  *
  * @author lhfcws 
  * @since 2018/1/15
  */
abstract class VisualRenderer(name: String, analyzRes: Seq[ParseResult]) {
  val YG = "YoungGen"
  val OG = "OldGen"
  val HEAP = "Heap"
  val FGC = "FullGC"
  val CMS = "CMS"

  def render(): Map[String, Any]

  def findMax(iterator: Iterator[ParseResult], key: String): (Double, ParseResult) = {
    var ret = 0.0
    var retPr: ParseResult = null

    while (iterator.hasNext) {
      val pr = iterator.next()
      val value = pr.params.getOrElse(key, null)
      if (value != null) {
        val cmpVal = value.asInstanceOf[Double]
        if (ret < cmpVal) {
          ret = cmpVal
          retPr = pr
        }
      }
    }
    return (ret, retPr)
  }

  def findMin(iterator: Iterator[ParseResult], key: String): (Double, ParseResult) = {
    var ret = Double.MaxValue
    var retPr: ParseResult = null

    while (iterator.hasNext) {
      val pr = iterator.next()
      val value = pr.params.getOrElse(key, null)
      if (value != null) {
        val cmpVal = value.asInstanceOf[Double]
        if (ret > cmpVal) {
          ret = cmpVal
          retPr = pr
        }
      }
    }
    return (ret, retPr)
  }

  def findFirstExists(iterator: Iterator[ParseResult], key: String): (Double, ParseResult) = {
    var ret = 0.0
    var retPr: ParseResult = null
    var flag = true

    while (iterator.hasNext && flag) {
      val pr = iterator.next()
      val value = pr.params.getOrElse(key, null)
      if (value != null) {
        ret = value.asInstanceOf[Double]
        retPr = pr
        flag = false
      }
    }
    return (ret, retPr)
  }

  def getDiffSeries(keys: Map[(String, String), String], timeKey: String = "xdata"): Map[String, Any] = {
    val times = ListBuffer[Long]()
    val stat = mutable.HashMap[String, Any]()

    for (key <- keys.values) {
      stat.put(key, ListBuffer[Double]())
    }

    for (pr <- analyzRes) {
      var findAny = false

      for (((beforeKey, afterKey), key) <- keys) {
        if (pr.params.contains(beforeKey) && pr.params.contains(afterKey)) {
          findAny = true
        }
      }

      if (findAny) {
        times += pr.time

        for (((beforeKey, afterKey), key) <- keys) {
          val lb = stat(key).asInstanceOf[ListBuffer[Double]]
          if (pr.params.contains(beforeKey) && pr.params.contains(afterKey)) {
            val before = pr.params(beforeKey).asInstanceOf[Double]
            val after = pr.params(afterKey).asInstanceOf[Double]
            val diff = before - after

            lb += diff
          } else {
            if (lb.isEmpty) {
              lb += 0
            } else {
              lb += lb.last
            }
          }
        }
      }
    }

    stat.put(timeKey, times)
    return stat.toMap
  }

  def getSeries(keys: Map[String, String], timeKey: String = "xdata"): Map[String, Any] = {
    val times = ListBuffer[Long]()
    val stat = mutable.HashMap[String, Any]()

    for (key <- keys.keys) {
      stat.put(keys(key), ListBuffer[Double]())
    }

    for (pr <- analyzRes) {
      var findAny = false
      for (key <- keys.keys) {
        if (pr.params.contains(key)) {
          findAny = true
        }

      }

      if (findAny) {
        for (key <- keys.keys) {
          val renameKey = keys(key)
          val lb = stat(renameKey).asInstanceOf[ListBuffer[Double]]

          if (pr.params.contains(key)) {
            lb += pr.params(key).asInstanceOf[Double]
          } else {
            if (lb.isEmpty) {
              lb += 0
            } else {
              lb += lb.last
            }
          }
        }

        times += pr.time
      }
    }

    stat.put(timeKey, times)
    return stat.toMap
  }
}

class GcStats {
  var empty = true

  var maxTime = 0.0
  var minTime = Double.MaxValue

  var count = 0
  var total = 0.0

  var intervalCount = 0
  var intervalTotal = 0.0

  var prevPr: ParseResult = _

  def apply(pr: ParseResult, parseFunc: Function[ParseResult, Option[Double]]): GcStats = {
    empty = false
    val opt = parseFunc.apply(pr)
    if (opt != null)
      for (v <- opt) {
        if (v > maxTime)
          maxTime = v
        if (v < minTime)
          minTime = v

        count += 1
        total += v

        if (prevPr != null) {
          intervalCount += 1
          intervalTotal += (pr.time - prevPr.time)
        }
        prevPr = pr
      }
    return this
  }

  def applyList(prs: Seq[ParseResult], parseFunc: Function[ParseResult, Option[Double]]): GcStats = {
    for (pr <- prs)
      apply(pr, parseFunc)
    return this
  }

  def toMap(): Map[String, AnyVal] = {
    var avgTime = 0.0
    var avgInterval = 0.0

    if (minTime == Double.MaxValue)
      minTime = 0.0

    if (count > 0)
      avgTime = total * 1.0 / count

    if (intervalCount > 0)
      avgInterval = intervalTotal * 1.0 / intervalCount

    return Map(
      "GC count" -> count,
      "GC total time" -> Util.precision2(total),
      "GC max time" -> Util.precision2(maxTime),
      "GC min time" -> Util.precision2(minTime),
      "GC avg time" -> Util.precision2(avgTime),
      "GC avg interval" -> Util.precision2(avgInterval)
    )
  }

  def isEmpty() = {
    empty
  }
}

object VisualRenderer {
  def createVisualRenderer(gcType: String, analyzeRes: Seq[ParseResult], filename: String): VisualRenderer = {
    if (gcType == "cms") {
      return new CMSVisualRenderer(filename, analyzeRes)
    } else
      // TODO for G1
      return null
  }
}

