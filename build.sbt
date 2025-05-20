import build.Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

ThisBuild / organization     := "com.itv.cacti"
ThisBuild / organizationName := "ITV"

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

ThisBuild / scalacOptions ++= List(
  "-Wunused"
)

addCommandAlias("fix", "scalafixAll; scalafmtAll; scalafmtSbt")

lazy val root = (project in file("."))
  .enablePlugins(ScalafixPlugin, ScalafmtPlugin)
  .settings(
    name := "alan-task",
    libraryDependencies ++= rootDeps
  )
  .aggregate(core, db, app)

lazy val db = (project in file("modules/db"))
  .settings(
    name := "alan-task-db",
    libraryDependencies ++= Seq(
    )
  )
  .dependsOn(core)

lazy val core = (project in file("modules/core"))
  .settings(
    name := "alan-task-core",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= coreDeps
  )

lazy val app = (project in file("app"))
  .settings(
    name := "alan-task-app",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= coreDeps
  )
  .dependsOn(core, db)
