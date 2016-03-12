name := "search-app-server"

organization := "name.sccu"

version := "0.1"

scalaVersion := "2.11.6"

libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided"

libraryDependencies += "org.apache.solr" % "solr-solrj" % "5.4.1"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.11.7"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.11.7"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.7.2"

libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.7.2"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.3.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.6"

libraryDependencies += "org.codehaus.janino" % "janino" % "2.7.8"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "latest.integration" % "test"

enablePlugins(JettyPlugin)

containerArgs := Seq("--path", "/search", "--classes", "./")

containerPort := 8086

//javaOptions += "-DsearchHandler=/search_handler.sc"
javaOptions += "-Dloglevel.debug"
