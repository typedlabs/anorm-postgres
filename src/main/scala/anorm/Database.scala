package anorm

import anorm.pooling.HikariCPPool
import com.zaxxer.hikari.HikariDataSource

import scala.util.control.{ NonFatal, ControlThrowable }

import java.sql.Connection
import net.sf.log4jdbc.ConnectionSpy

import com.typesafe.config._
import configs.syntax._

case class Database(val name: String, private val conf: Config, prefix: String = "") {

  val config = conf.get[Config](s"anorm.db.${name}").toOption.get

  private lazy val pooling: HikariCPPool = 
    new HikariCPPool(name, conf.get[Config](s"anorm.db").toOption.get)

  lazy val logEnabled = config.get[Boolean]("logSql") valueOrElse false

  lazy val pooledDataSource: HikariDataSource = pooling.getDataSource

  def withConnection[A](block: Connection => A): A = {

    val originalConnection: Connection = pooledDataSource.getConnection()

    val conn = 
      if(logEnabled)
        new ConnectionSpy(originalConnection)
      else 
        originalConnection


    try {
      block(conn)
    } catch {
      case e: Exception =>
        conn.close()
        throw e
    } finally {
      conn.close()
    }

  }

  def withTransaction[A](block: Connection => A): A = {
    withConnection{ connection =>
      try {
        connection.setAutoCommit(false)
        val r = block(connection)
        connection.commit()
        r
      } catch {
        case e: ControlThrowable =>
          connection.commit(); throw e
        case NonFatal(e) => connection.rollback(); throw e
      }
    }
  }
}
