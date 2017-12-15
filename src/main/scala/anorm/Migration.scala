package anorm

import org.flywaydb.core.Flyway

class Migration(db: Database) {
  
  val flyway: Flyway = new Flyway()
  
  flyway.setDataSource(db.pooledDataSource)

  def migrate: Int = {    
    flyway.setLocations(s"db/migration/${db.name}")
    flyway.migrate()
  }

  def clean(): Unit = flyway.clean()

}