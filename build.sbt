ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "kafkaLoggingSystem"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.4.0"
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "mysql" % "mysql-connector-java" % "8.0.32"
)
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1"
libraryDependencies += "io.github.embeddedkafka" %% "embedded-kafka" % "3.4.0.1" % Test
