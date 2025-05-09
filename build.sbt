import build.Dependencies._

Global / onChangedBuildSource    := ReloadOnSourceChanges

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

ThisBuild / organization := "com.itv.cacti"
ThisBuild / organizationName := "ITV"


lazy val root = (project in file("."))
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


/**
* Task 1
* Add http4s dependencies also , bring new library to the scope , fs2 !
* */
lazy val app = (project in file("app"))
  .settings(
    name := "alan-task-app",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= coreDeps
  ).dependsOn(core, db)
