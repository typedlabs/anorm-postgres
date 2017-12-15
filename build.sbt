name := "anormPostgres"

organization := "com.typedlabs"

version := "0.1.1"

scalaVersion := "2.12.4"

crossScalaVersions := Seq(scalaVersion.value, "2.11.8")

bintrayRepository := "releases"

bintrayOrganization := Some("typedlabs")

bintrayPackageLabels := Seq("scala", "anorm", "postgresql", "sql", "postgres")

bintrayVcsUrl := Some("https://github.com/typedlabs/anorm-postgres")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

publishMavenStyle := false


scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-deprecation",
    "-unchecked",
    "-feature",
    "-language:postfixOps",      
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-Xlint",
    "-Ydelambdafy:method",
    "-target:jvm-1.8",
    "-language:existentials",
    "-language:implicitConversions",
    "-Xfatal-warnings"
  )

libraryDependencies ++= Seq(
  //-- Logging
  "com.typesafe.scala-logging" %% "scala-logging"    % "3.5.0",
  "com.googlecode.log4jdbc"    % "log4jdbc"          % "1.2",

  //-- CONFIGS
  "com.typesafe"               % "config"            % "1.3.1",
  "com.github.kxbmap"          %% "configs"          % "0.4.4",

  //-- PSQL
  "org.postgresql"             % "postgresql"        %  "42.1.4",

  //-- MIGRATIONS
  "org.flywaydb"               % "flyway-core"       % "4.2.0",

  //-- POOLING
  "com.zaxxer"                 % "HikariCP"          % "2.6.3",

  //-- ANORM
  "com.typesafe.play"          %% "anorm"            % "2.5.3",

  //-- JSON
  "com.typesafe.play"         %% "play-json"        % "2.6.3",

  //-- PostGis
  "net.postgis"               %  "postgis-jdbc"     % "2.2.1",

  //-- TEST
  "org.scalatest"     %% "scalatest"        % "3.0.0" % "test"
)

