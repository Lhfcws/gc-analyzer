package test

import com.datastory.gc.visual.webserver.LocalFilepathHandler
import org.scalatest.FunSpec

/**
  *
  * test.LocalFilePathHandlerTest
  *
  * @author lhfcws 
  * @since 2018/1/18
  */
class LocalFilePathHandlerTest extends FunSpec{
  describe("LocalFilePathHandlerTest") {
    it("should successfully parse to a renderable json ") {
      val resJson = new LocalFilepathHandler()._handle("cms", "/Users/lhfcws/coding/workspace/gc-analyzer/src/test/resources/gc.parnew.log")
      print(resJson)
    }
  }
}
