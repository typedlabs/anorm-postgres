package anorm.pooling

import scala.collection.JavaConverters._

import com.zaxxer.hikari.HikariDataSource
import com.typesafe.scalalogging.LazyLogging
import com.typesafe.config.Config

class HikariCPPool(name: String, configuration: Config) extends LazyLogging {

  lazy val dataSourceConfigs = configuration.root().keySet().asScala.toSet
    .map { dataSourceName: String =>
      val dataSourceConfig = configuration.getConfig(dataSourceName)
      dataSourceName -> dataSourceConfig
    }

  val datasources: List[(HikariDataSource, String)] = dataSourceConfigs.map {
    case (dataSourceName, dataSourceConfig) =>

      logger.info(s"Creating Pool for datasource '$dataSourceName'")

      val hikariConfig = HikariCPConfig.toHikariConfig(dataSourceName, dataSourceConfig)

      val dataSource: HikariDataSource = new HikariDataSource(hikariConfig)
      dataSource -> dataSourceName

  }.toList

  def getDataSource: HikariDataSource = {
    datasources
      .find { case (_, dsName) => dsName == name }
      .map { case (ds, _) => ds }
      .getOrElse(sys.error(s" - could not find data source for name $name"))
  }

}