import sbt._
import sbt.Keys._
import com.typesafe.sbt.rjs.Import.rjs
import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbt.web.Import.{Assets, pipelineStages}
import com.typesafe.sbt.digest.Import.digest
import com.typesafe.sbt.gzip.Import.gzip
import com.typesafe.sbt.less.Import.LessKeys
import com.typesafe.sbt.jse.JsEngineImport.JsEngineKeys

object ScalataireBuild extends Build with UniversalKeys {
  val id = "scalataire"

  val dependencies = {
    import Dependencies._
    Seq(
      Database.jdub,
      Database.postgresJdbc,

      Metrics.metrics,
      Metrics.metricsJvm,
      Metrics.metricsHealthChecks,
      Metrics.metricsJson,
      Metrics.jettyServlet,
      Metrics.metricsServlets,
      Metrics.metricsGraphite,

      WebJars.requireJs
    )
  }

  private val compileOptions = Seq("-encoding", "UTF-8", "-feature", "-deprecation", "-unchecked")

  object Versions {
    val app = "0.1-SNAPSHOT"
    val scala = "2.11.5"
  }

  lazy val serverSettings = Seq(
    name := id,
    version := Versions.app,
    scalaVersion := Versions.scala,
    libraryDependencies ++= dependencies,
    pipelineStages := Seq(rjs, digest, gzip),
    includeFilter in (Assets, LessKeys.less) := "*.less",
    excludeFilter in (Assets, LessKeys.less) := "_*.less",
    scalacOptions ++= compileOptions,
    JsEngineKeys.engineType := JsEngineKeys.EngineType.Node
  )

  lazy val root = Project(
    id = id,
    base = file(".")
  ).enablePlugins(play.PlayScala, SbtWeb).settings(serverSettings: _*)
}
