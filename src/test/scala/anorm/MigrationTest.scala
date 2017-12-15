package anorm

import org.scalatest._
import com.typesafe.config.ConfigFactory

class MigrationTestSpec extends FlatSpec with BeforeAndAfter with Matchers {

  val config = ConfigFactory.load("application-test.conf")

  val database = new Database("test", config)

  val migration = new Migration(database)

  before {
    migration.clean
  }

  "Migration" should "find 1 Migration" in {
    migration.flyway.info().all.length should be (1)
  }  

  "Migration" should "create table test_table" in {
    
    migration.migrate

    database.withConnection{ implicit conn =>
      SQL"""select count(*) from schema_version"""
        .as(SqlParser.scalar[Long].singleOpt)
    } should be (Some(1))
  }  

  "Migration" should "cleanup and delete all tables" in {
    migration.clean()

    database.withConnection{ implicit conn =>
      SQL"""
        SELECT count(*) FROM pg_catalog.pg_tables 
        WHERE schemaname != 'pg_catalog' 
        AND schemaname != 'information_schema';
      """
        .as(SqlParser.scalar[Long].singleOpt)
    } should be (Some(0))

  }    

}