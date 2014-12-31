import java.io.File

import sbt._
import Keys._

//import com.mojolly.scalate.ScalatePlugin._

object BuildSettings {
  val buildOrganization = "dev.example"
  val buildVersion      = "0.1-SNAPSHOT"
  val buildScalaVersion = "2.11.4"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )
}

//object TemplateSettings {
//  val templateSettings = scalateSettings ++ Seq(
//    scalateTemplateDirectory.in(Compile) := new File("src/main/webapp/WEB-INF")
//  )
//}

object Resolvers {
  val typesafeSanpshots =  "snapshots"      at "http://oss.sonatype.org/content/repositories/snapshots"
  val typesafeRepo      = "Typesafe Repo"   at "http://repo.typesafe.com/typesafe/releases/"
  val typesafeReleasesRepo  = "releases"            at "http://oss.sonatype.org/content/repositories/releases"
  //val journalioRepo = "Journal.IO Repo" at "https://raw.github.com/sbtourist/Journal.IO/master/m2/repo"
}

object Versions {
  val Akka   = "2.3.8"
  val Jersey = "1.18.3"
  val Jetty  = "9.2.6.v20141205"
  val Netty  = "3.2.10.Final"
  val Spring = "4.1.3.RELEASE"
}

object Dependencies {
  import Versions._

  // compile dependencies
  lazy val akkaActor    = "com.typesafe.akka"         % "akka-actor_2.11"    % Akka    % "compile"
  lazy val akkaAgent    = "com.typesafe.akka"         % "akka-agent_2.11"    % Akka    % "compile"
  lazy val akkaStm      = "org.scala-stm"             % "scala-stm_2.11"    % "0.7"   % "compile"
  //lazy val journalio    = "journalio"                  % "journalio"         % "1.4.2" % "compile"
  lazy val journalio    = "com.github.sbtourist"       % "journalio"         % "1.4.2" % "compile"
  lazy val jsr311       = "javax.ws.rs"                % "jsr311-api"        % "1.1.1" % "compile"
  lazy val jerseyCore   = "com.sun.jersey"             % "jersey-core"       % Jersey  % "compile"
  lazy val jerseyJson   = "com.sun.jersey"             % "jersey-json"       % Jersey  % "compile"
  lazy val jerseyServer = "com.sun.jersey"             % "jersey-server"     % Jersey  % "compile"
  lazy val jerseySpring = "com.sun.jersey.contribs"    % "jersey-spring"     % Jersey  % "compile"
  lazy val netty        = "org.jboss.netty"            % "netty"             % Netty   % "compile"
//  lazy val scalate      = "org.fusesource.scalate"     % "scalate-core"    % "1.5.2" % "compile"
  lazy val scalaz       = "org.scalaz"                 % "scalaz-core_2.11"  % "7.1.0" % "compile"
  lazy val springWeb    = "org.springframework"        % "spring-web"        % Spring  % "compile"
  lazy val zookeeper    = "org.apache.zookeeper"       % "zookeeper"         % "3.4.6" % "compile"

  // container dependencies TODO: switch from "compile" to "container" when using xsbt-web-plugin
  lazy val jettyServer  = "org.eclipse.jetty"          % "jetty-server"      % Jetty   % "compile"
  lazy val jettyServlet = "org.eclipse.jetty"          % "jetty-servlet"     % Jetty   % "compile"
  lazy val jettyWebapp  = "org.eclipse.jetty"          % "jetty-webapp"      % Jetty   % "compile"

  // runtime dependencies
  lazy val configgy  = "net.lag" % "configgy" % "2.0.0" % "runtime"

  // test dependencies
  lazy val scalatest = "org.scalatest" % "scalatest_2.11" % "2.2.2" % "test"
}

object ExampleBuild extends Build {
  import BuildSettings._
  //import TemplateSettings._
  import Resolvers._
  import Dependencies._

  lazy val example = Project (
    "eventsourcing-example",
    file("."),
    settings = buildSettings ++ /*templateSettings ++*/ Seq (
      resolvers            := Seq (typesafeRepo, typesafeSanpshots, typesafeReleasesRepo/*, journalioRepo*/),
      // compile dependencies (backend)
      libraryDependencies ++= Seq (akkaActor, akkaAgent, akkaStm, journalio, netty, scalaz, zookeeper),
      // compile dependencies (frontend)
      libraryDependencies ++= Seq (jsr311, jerseyCore, jerseyJson, jerseyServer, jerseySpring, springWeb/**, scalate*/),
      // container dependencies
      libraryDependencies ++= Seq (jettyServer, jettyServlet, jettyWebapp),
      // runtime dependencies
      libraryDependencies ++= Seq (configgy),
      // test dependencies
      libraryDependencies ++= Seq (scalatest),
      // execute tests sequentially
      parallelExecution in Test := false
    )
  )
}
