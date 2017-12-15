package anorm.extensions

import anorm._

case class PostGISGeography(latitude: Double, longitude: Double)

trait AnormPostGIS {

  implicit def rowToGeoLocation: Column[PostGISGeography] = Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, _, _) = meta

      value match {
        case pgo: org.postgresql.util.PGobject => {          
          val geom = new org.postgis.PGgeometry(pgo.getValue).getGeometry
          val point = geom.getFirstPoint
          Right(PostGISGeography(point.x, point.y))
        }
        case _ => Left(TypeDoesNotMatch("Cannot convert " + value + " : " + 
          value.asInstanceOf[AnyRef].getClass + " GeoLocation for column" + qualified))
      }
  }

  // implicit def rowToOptGeoLocation: Column[Option[PostGISGeography]] = Column.nonNull { (value, meta) =>
  //     val MetaDataItem(qualified, _, _) = meta

  //     value match {
  //       case pgo: org.postgresql.util.PGobject => {          
  //         val geom = new org.postgis.PGgeometry(pgo.getValue).getGeometry
  //         val point = geom.getFirstPoint
  //         Right(Some(PostGISGeography(point.x, point.y)))
  //       }
  //       case _ => Left(TypeDoesNotMatch("Cannot convert " + value + " : " + 
  //         value.asInstanceOf[AnyRef].getClass + " GeoLocation for column" + qualified))
  //     }
  // }

  // implicit object optGeoToStatement extends ToStatement[Option[PostGISGeography]] {
  //   def set(s: PreparedStatement, i: Int, geo: Option[PostGISGeography]): Unit = {
  //     val jsonObject = new org.postgresql.util.PGobject()
  //     jsonObject.setType("json")

  //     json.map { json =>
  //       jsonObject.setValue(Json.stringify(json))
  //     }.getOrElse {
  //       jsonObject.setValue("{}")
  //     }
  //     s.setObject(i, jsonObject)
  //   }
  // }


}