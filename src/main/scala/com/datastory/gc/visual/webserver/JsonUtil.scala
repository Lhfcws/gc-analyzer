package com.datastory.gc.visual.webserver
import com.fasterxml.jackson.core.{JsonGenerator, Version}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{JsonNode, JsonSerializer, ObjectMapper, SerializerProvider}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

/**
  * Json序列化与反序列化，使用jackson-scala
  * com.datastory.gc.visual.webserver.JsonUtil
  *
  * @author lhfcws 
  * @since 2018/1/18
  */
object JsonUtil {
  /**
    * The Jackson ObjectMapper instance that is initialized with serializers for a few special cases:
    *
    * 1. rest.li RecordTemplates and DataMaps
    * 2. Special handling for numbers to avoid JavaScript overflow errors
    * 3. Play F.Option class
    *
    * More info on rest.li RecordTemplates: these classes have conflicting methods for a single field (e.g. getFoo and isFoo)
    * that Jackson cannot handle. The getters also throw an exception if a "required" field is null, but we don't
    * necessarily want this validation when converting the object to JSON. Therefore, we add a couple Jackson serializers
    * to handle RecordTemplate and DataMap objects.
    *
    */
  val mapper = new ObjectMapper()

  private val serializerModule = new SimpleModule("JsonUtilSerializerModule", new Version(1, 0, 0, null, null, null))

  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(serializerModule)

  def dumpJson(obj: Map[String, Any]): String = {
    if (obj == null) {
      "{}"
    } else {
      mapper.writeValueAsString(obj)
    }
  }

  def fromJson(s : String): Map[String, Any] = {
    mapper.readValue(s, classOf[Map[String, Any]])
  }
}
