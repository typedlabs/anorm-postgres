package anorm.pooling

import com.zaxxer.hikari.HikariConfig
import com.typesafe.config.Config
import java.net.URI
import scala.concurrent.duration.FiniteDuration
import com.typesafe.scalalogging.LazyLogging
import configs.syntax._

import scala.collection.JavaConverters._


object HikariCPConfig extends LazyLogging {

  def toHikariConfig(dataSourceName: String, config: Config): HikariConfig = {

    val hikariConfig = new HikariConfig()

    // Essentials configurations
    config.get[String]("dataSourceClassName").toOption match {
      case Some(className) => hikariConfig.setDataSourceClassName(className)
      case None => logger.debug("`dataSourceClassName` not present. Will use `jdbcUrl` instead.")
    }

    config.get[String]("jdbcUrl").toOption match {
      case Some(jdbcUrl) => hikariConfig.setJdbcUrl(jdbcUrl)
      case None => logger.debug("`jdbcUrl` not present. Pool configured from `databaseUrl`.")
    }

    config.get[String]("databaseUrl").toOption match {
      case Some(databaseUrl) => {
        val dbUri = new URI(databaseUrl)
        val dbScheme = dbUri.getScheme match {
          case "postgres" => "postgresql"
          case scheme => scheme
        }
        hikariConfig.setJdbcUrl(s"jdbc:${dbScheme}://${dbUri.getHost}:${dbUri.getPort}${dbUri.getPath}")
        hikariConfig.setUsername(dbUri.getUserInfo.split(":")(0))
        hikariConfig.setPassword(dbUri.getUserInfo.split(":")(1))
      }
      case None => logger.debug("`databaseUrl` not present. Will use `dataSourceClassName` instead.")
    }

    config.get[Config]("dataSource").foreach { dataSourceConfig =>
      dataSourceConfig.entrySet.asScala.map(_.getKey).toSet.foreach { key: String =>
        hikariConfig.addDataSourceProperty(key, dataSourceConfig.get[String](key).toOption.get)
      }
    }
  
    config.get[String]("username").foreach(hikariConfig.setUsername)
    config.get[String]("password").foreach(hikariConfig.setPassword)
    config.get[String]("driverClassName").foreach(hikariConfig.setDriverClassName)

    // Frequently used
    config.get[Boolean]("autoCommit").foreach(hikariConfig.setAutoCommit)
    
    config.get[FiniteDuration]("connectionTimeout").map(_.toMillis).foreach(hikariConfig.setConnectionTimeout)
    config.get[FiniteDuration]("idleTimeout").map(_.toMillis).foreach(hikariConfig.setIdleTimeout)
    config.get[FiniteDuration]("maxLifetime").map(_.toMillis).foreach(hikariConfig.setMaxLifetime)
    
    config.get[String]("connectionTestQuery").foreach(hikariConfig.setConnectionTestQuery)
    config.get[Int]("minimumIdle").foreach(hikariConfig.setMinimumIdle)
    config.get[Int]("maximumPoolSize").foreach(hikariConfig.setMaximumPoolSize)    
    hikariConfig.setPoolName(config.get[String]("poolName").toOption.getOrElse(dataSourceName))

    // Infrequently used
    // config.get[Boolean]("initializationFailFast").foreach(hikariConfig.setInitializationFailFast)
    // config.get[String]("isolateInternalQueries").foreach(hikariConfig.setIsolateInternalQueries)
    // config.get[String]("allowPoolSuspension").foreach(hikariConfig.setAllowPoolSuspension)
    // config.get[String]("readOnly").foreach(hikariConfig.setReadOnly)
    // config.get[Boolean]("registerMbeans").foreach(hikariConfig.setRegisterMbeans)
    // config.get[String]("catalog").foreach(hikariConfig.setCatalog)
    // config.get[String]("connectionInitSql").foreach(hikariConfig.setConnectionInitSql)
    // config.get[String]("transactionIsolation").foreach(hikariConfig.setTransactionIsolation)
    // config.opt[Duration]("validationTimeout").foreach(hikariConfig.setValidationTimeout)
    // config.opt[Duration]("leakDetectionThreshold").foreach(hikariConfig.setLeakDetectionThreshold)

    hikariConfig
  }
}