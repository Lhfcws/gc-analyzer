package com.datastory.gc.analyze

/**
  *
  * com.datastory.gc.analyze.LocalParser
  *
  * @author lhfcws 
  * @since 2018/1/8
  */
class LocalParser(line: String) {
  // ptr means pointer
  var ptr: Int = 0

  def locatePtrAtEnd(locateStr: String): Boolean = {
    val i = line.indexOf(locateStr)
    if (i == -1) {
      return false
    } else {
      ptr = i + locateStr.length
      return true
    }
  }

  def addPtr(i: Int): Int = {
    ptr += i
    return ptr
  }

  def extractFromPtrUntilLengthIs(len: Int): String = {
    val ret = line.substring(ptr, ptr + len)
    ptr += len
    return ret
  }

  def extractFromPtrUntilCharIs(stopChar: Char): String = {
    val sb = new StringBuilder()
    try {
      while (line.charAt(ptr) != stopChar) {
        sb.append(line.charAt(ptr))
        ptr += 1
      }
      return sb.toString()
    } finally {
      sb.clear()
    }
  }
}
