package test

import org.scalatest.FunSpec
import com.datastory.gc.visual.webserver.JsonUtil

/**
  *
  * test.ScalaJsonTest
  *
  * @author lhfcws 
  * @since 2018/1/18
  */
class ScalaJsonTest extends FunSpec {
  describe("ScalaJsonTest") {
    it("should successfully dump into json ") {
      val o = Map[String, Any](
        "_comments" -> "it is a fake object",
        "o1" -> Map[String, Any](
          "nested1" -> "1",
          "paper" -> 1239234,
          "double" -> 3.24,
          "zero" -> 100.0
        )
      )

      val json = JsonUtil.dumpJson(o)
      print(json)
    }

    it("should successfully parsed from json ") {
      val json = "{\"_comments\":\"it is a fake object\",\"o1\":{\"nested1\":\"1\",\"paper\":1239234,\"double\":3.24,\"zero\":100.0}}"
      val obj = JsonUtil.fromJson(json)
      print(obj)
    }
  }
}
