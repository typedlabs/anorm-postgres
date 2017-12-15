# anorm-postgres
Anorm Toolkit and Extensions to work with PostgreSQL database


1. [SBT](#sbt)
2. [Connect](#connect)
2. [Migrations](#migrations)
4. [Supported Extensions](#supported-extensions)
4. [Util](#util)

## Quick start

### SBT

[ ![Download](https://api.bintray.com/packages/typedlabs/releases/anorm-postgres/images/download.svg) ](https://bintray.com/typedlabs/releases/anorm-postgres/_latestVersion)

In `plugins.sbt`, add the following bintray resolver

```scala
Resolver.bintrayIvyRepo("typedlabs", "releases")
```

## Connect

To connect to a postgresql database you can add the following settings on an application.conf file

~~~scala
# application.conf
anorm.db.test.driverClassName = "org.postgresql.Driver"
anorm.db.test.jdbcUrl = "jdbc:postgresql://localhost:5432/test"
anorm.db.test.username=dbadmin
anorm.db.test.password=dbadmin
anorm.db.test.maximumPoolSize=5
anorm.db.test.logSql=false
~~~

Then you create an **anorm.Database** object.

~~~scala
  val config = ConfigFactory.load("application.dev.conf")
  val database = Database("test", config)
~~~

with **anorm.Database** you can query and get the results:

~~~scala
  database.withConnection{ implicit connection =>
    SQL"select 1 as total"
      .as(SqlParser.scalar[Long].singleOpt)
  }

  database.withTransaction{ implicit connection =>
    SQL"select 1 as total"
      .as(SqlParser.scalar[Long].singleOpt)
  }  
~~~

## Migrations
anorm-postgres also has a migration API based on **Flyway**, migration files will be found on the resources folder under **s"db/migration/${db.name}"**.

To run migration you just pass the **database** object.

~~~scala
val migration = new Migration(database)

// Careful, it clears all database, useful for testing
migration.clean

migration.migrate

~~~

## Supported Extensions
* HStore
* LTree
* PostGIS
* Json(play-json)

## Util
anorm-postgres also has an util class DAO[A] which has some useful boilerplate when working with anorm.

