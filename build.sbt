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

/** Task 2
  *
  * We need to be able to interact with out PostgreSQL and for that we need to
  * bring some more libraries
  *
  * Our weapon of choice will be Doobie https://typelevel.org/doobie/index.html
  *
  * Doobie provides a TTransactor object that facilitates the communication and
  * lets us write SQL statements in Scala
  *
  * we will need doobie core , doobie postgre , doobie hikari ( implementation
  * of transactor ) and doobie circe to use our decoders and encoder to write
  * and read from the DB.
  */

lazy val db = (project in file("modules/db"))
  .settings(
    name := "alan-task-db",
  )
  .dependsOn(core)

lazy val core = (project in file("modules/core"))
  .settings(
    name := "alan-task-core",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= coreDeps
  )

/** Task 1
  *
  * We need Ciris to be able to read from the environment and build the config ,
  * update the core deps to include it!
  *
  * Ciris is a standard library used to interact with he environment in which
  * our app runs
  */
lazy val app = (project in file("app"))
  .settings(
    name := "alan-task-app",
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    libraryDependencies ++= coreDeps
  )
  .dependsOn(core, db)
