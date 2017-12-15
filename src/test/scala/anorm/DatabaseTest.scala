package anorm

import org.scalatest._
import com.typesafe.config.ConfigFactory

class DatabaseTestSpec extends FlatSpec with Matchers  {

  val config = ConfigFactory.load("application-test.conf")

  val database = Database("test", config)

  "Database" should "connect via config" in {

    database.name should be ("test")

    database.logEnabled should be (false)

  }

  "SqlQuery" should "be completed" in {

    val selectTest = 
      database.withConnection{ implicit connection =>
        SQL"select 1 as total"
          .as(SqlParser.scalar[Long].singleOpt)
      }

    selectTest should be (Some(1))

  }  


}