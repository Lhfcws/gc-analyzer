package com.datastory.gc.analyze

import scala.collection.mutable

/**
  *
  * com.datastory.gc.analyze.ParseResult
  *
  * @author lhfcws 
  * @since 2018/1/8
  */
class ParseResult(var time: Long) extends Cloneable {
  val params: mutable.HashMap[String, AnyVal] = new mutable.HashMap[String, AnyVal]()

  override def clone(): ParseResult = {
    val pr = new ParseResult(this.time)
    pr.params ++= this.params
    return pr
  }

  def contains(key: String): Boolean = {
    return params.contains(key)
  }

  def put(key: String, value: AnyVal): ParseResult = {
    this.params.put(key, value)
    return this
  }

  def get(key: String): AnyVal = {
    return params(key)
  }

  def getInt(key: String): Option[Int] = {
    if (params.contains(key)) {
      val v = params(key).asInstanceOf[Int]
      return Some(v)
    } else
      return null
  }

  def getDouble(key: String): Option[Double] = {
    if (params.contains(key)) {
      val v = params(key).asInstanceOf[Double]
      return Some(v)
    } else
      return null
  }

  def merge(pr: ParseResult): ParseResult = {
    if (this.time <= 0)
      this.time = pr.time
    this.params ++= pr.params
    return this
  }

}

