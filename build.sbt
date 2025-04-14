/**
 *
 * build.sbt is the build defining file , no surprises here
 * Even though the syntax might look a little bit strange we are still writing Scala here
 * SBT is not the best build tool but it gets the jobs done
 * and its mostly used build tool for Scala project.
 * The other alternative is Mill
 *
 * */


ThisBuild / version := "0.1.0-SNAPSHOT"


/**
 *
 * There is Scala 2 and Scala 3. Most of the projects in general still run on scala 2
 * Scala 3 has new syntax and changes quite a few things. It is an improvement , its just the migration is a pain
 * Scala 2.13.16 is the latest version of Scala 2 and that what we are going to use.
 * Same goes to all Producer Portal BE codebases except of producers-auth-api.
 * */


ThisBuild / scalaVersion := "2.13.16"

ThisBuild / organization     := "com.itv.cacti"
ThisBuild / organizationName := "ITV"

/**
 * Sbt allows you to define modules that got some code in it
 * The great thing about is is that each module is compiled in parallel
 * so you can get really fast compilation time if each logical piece of your code is separated into a module
 * This architectural approach is called diamond architecture and it usually follows this structure more or less:
 *
 * 1. There is one 'core" module , that has all the common modules used across the app
 * it should be the smallest module of your code , usually with interfaces only and few models with minimal library dependencies
 *
 * 2. Other modules that depend on core are business logic related modules . like images-api module or documents-api
 * Those modules depend on core and sometimes on a db layer if necessary. The idea is that logic modules have classes
 * that are only needed for them and nothing else. Each time you have new piece of business logic ideally it would end up in its own module
 * so you can use the most out of the parallel computation.
 *
 * 3. "App" module is the one module to rule them all. It wires up all the other modules into something usefully
 * If you have module A that depends on B and module C that depends on B , module C has access both to module A and B
 *
 * */


/**
 * SBT is huge and I think there is no point od delving into all its capabilities at this point.
 * For the purpose of this task we will just use modules , setting on those modules and library dependencies only.
 * */


/** TODO
 * Task 1
 * We will need some library to start going.
 * Your task will be to wire the modules correctly and following dependencies
 *  - cats core
 *  - cats effect
 *  - circe literals
 *  - circe core
 *  - circe generics
 *  - ciris
 *  - http4s core
 *  - http4s-dsl
 *  - http4s-ember-server
 *  - http4s-circe
 *  - enumeratum
 *  - enumeratum circe
 *
 *  and weaver-cats fot tests ( Note , look how to define test libraries! There is a key word for that)
 *
 *  The dependencies are passed as a Seq to libraryDependencies settings field
 * */


  // root only aggregates the modules and adds project wide settings
lazy val root = (project in file("."))
  .settings(
    name := "alan-task",
    libraryDependencies ++= Seq()
  ).aggregate()


/**
 * At the moment we will use in memory storage so there is no need to change anything here
 * */
lazy val db = (project in file("modules/db"))
  .settings(
    name := "alan-task-db",
    libraryDependencies ++= Seq(
    )
  ).dependsOn()


/**
 * For core we will need whatever needed to define the model
 * encoders and decoders for the model
 * and test framework to check if the codecs work as we want
 * */
// Stores domain models used across all other modules
lazy val core = (project in file("modules/core"))
  .settings(
    name := "alan-task-core",
    libraryDependencies ++= Seq(
    )
  )


/**
 *
 *  TODO
 * Task 2
 * - Implement new module and wire it with others. Explore other methods on a module provided from sbt
 *
 * The app module will be the place where the server , routes and config will be created.
 *
 *
 *  Hint.2
 *
 * */
lazy val app = ???


