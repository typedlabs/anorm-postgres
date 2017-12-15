package anorm.extensions

import play.api.libs.json.{ Json, JsValue }
import java.sql.{ PreparedStatement }

import anorm._

trait AnormJson {

  implicit object jsonToStatement extends ToStatement[JsValue] {
    def set(s: PreparedStatement, i: Int, json: JsValue): Unit = {
      val jsonObject = new org.postgresql.util.PGobject()
      jsonObject.setType("json")
      jsonObject.setValue(Json.stringify(json))
      s.setObject(i, jsonObject)
    }
  }

  implicit object optJsonToStatement extends ToStatement[Option[JsValue]] {
    def set(s: PreparedStatement, i: Int, json: Option[JsValue]): Unit = {
      val jsonObject = new org.postgresql.util.PGobject()
      jsonObject.setType("json")

      json.map { json =>
        jsonObject.setValue(Json.stringify(json))
      }.getOrElse {
        jsonObject.setValue("{}")
      }
      s.setObject(i, jsonObject)
    }
  }

  implicit val columnToJsValue: Column[JsValue] = anorm.Column.nonNull[JsValue] { (value, meta) =>
    val MetaDataItem(qualified, _, _) = meta
    // val MetaDataItem(qualified, nullable, clazz)=meta
    value match {
      case json: org.postgresql.util.PGobject => Right(Json.parse(json.getValue))
      case _ => Left(TypeDoesNotMatch(s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to Json for column $qualified"))
    }
  }

}