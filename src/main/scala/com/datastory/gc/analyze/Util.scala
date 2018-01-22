package com.datastory.gc.analyze

/**
  *
  * com.datastory.gc.analyze.Util
  *
  * @author lhfcws 
  * @since 2018/1/9
  */
object Util {
  def kbToMb(kb: Int): Double = {
    return Util.precision2(kb * 1.0 / 1024)
  }

  def secToMs(sec: Double): Double = {
    return sec * 1000
  }

  def precision2(d: Double): Double = {
    Math.floor(d * 100) / 100.0
  }
}
