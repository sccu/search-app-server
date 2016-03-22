name := "search-app-server"

organization := "name.sccu"

version := "0.1"

val scalaVer = "2.11.7"

scalaVersion := scalaVer

resolvers += "spring-releases" at "http://repo.spring.io/libs-release-remote/"

libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"

libraryDependencies += "org.apache.solr" % "solr-solrj" % "5.5.0"

libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVer

libraryDependencies += "org.scala-lang" % "scala-library" % scalaVer

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVer

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.7.2"

libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.7.2"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.3.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.6"

libraryDependencies += "org.codehaus.janino" % "janino" % "2.7.8"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"


libraryDependencies += "org.scalatest" %% "scalatest" % "latest.integration" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.10" % "test"

libraryDependencies += "org.apache.solr" % "solr-test-framework" % "5.5.0" % "test" exclude(
    "com.fasterxml.jackson.core", "jackson-core"
  )


enablePlugins(JettyPlugin)

containerArgs := Seq("--path", "/search", "--classes", "./")

fork := true

val port = 8086

containerPort := port

javaOptions += "-Dloglevel.debug"

javaOptions in Test ++= Seq(
  "-Dloglevel.debug",
  s"-Dtest.servlet.port=$port",
  "-DsearchHandler=/test-search-handler.sc")

