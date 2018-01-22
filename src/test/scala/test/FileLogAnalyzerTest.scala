package test

import com.datastory.gc.analyze.FileLogAnalyzer
import org.scalatest.FunSpec

/**
  *
  * test.FileLogAnalyzerTest
  *
  * @author lhfcws 
  * @since 2018/1/9
  */
class FileLogAnalyzerTest extends FunSpec {
  describe("FileLogAnalyzerTest") {
    it("should successfully parse logs ") {
      val analyzer = new FileLogAnalyzer("/Users/lhfcws/coding/workspace/gc-analyzer/src/test/resources/gc.parnew.log", "cms")
      val res = analyzer.analyze()
      print(res)
      assert(!res.isEmpty)
    }
  }
}
