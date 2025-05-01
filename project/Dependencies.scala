package build

import sbt._
import sbt.Keys._

object Dependencies {
  import Modules._

  lazy val rootDeps = Seq(
    cats.core,
    cats.effect,
  )

  lazy val coreDeps = Seq(
    cats.core,
    cats.effect,
    circe.core,
    circe.generic,
    circe.literal,
    enumeratum.core,
    enumeratum.circe,
    weaver.cats,
    jawn.core
  )


  object Modules {
    object cats {
      val core = "org.typelevel" %% "cats-core" % "2.9.0"
      val effect = "org.typelevel" %% "cats-effect" % "3.4.9"
      val mtl = "org.typelevel" %% "cats-mtl" % "1.3.0"
      val kittens = "org.typelevel" %% "kittens" % "3.0.0"
      val mouse = "org.typelevel" %% "mouse" % "1.2.1"
    }

    object circe {
      val core = "io.circe" %% "circe-core" % "0.14.3"
      val generic = "io.circe" %% "circe-generic" % "0.14.3"
      val literal = "io.circe" %% "circe-literal" % "0.14.3"
    }

    object http4s {
      val org = "org.http4s"
      val v = "0.23.26"

      val core = org %% "http4s-core" % v
      val dsl = org %% "http4s-dsl" % v
      val emberServer = org %% "http4s-ember-server" % v
      val emberClient = org %% "http4s-ember-client" % v
      val circe = org %% "http4s-circe" % v
    }

    object enumeratum {
      val v = "1.7.2"

      val core = "com.beachape" %% "enumeratum" % v
      val doobie = "com.beachape" %% "enumeratum-doobie" % "1.7.5"
      val circe = "com.beachape" %% "enumeratum-circe" % v

    }

    object weaver {
      private lazy val org = "com.disneystreaming"
      private lazy val version = "0.8.1"

      lazy val cats = org %% "weaver-cats" % version % Test
      lazy val scalacheck = org %% "weaver-scalacheck" % version % Test
      lazy val discipline = org %% "weaver-discipline" % version % Test

      lazy val test = Seq(cats, scalacheck, discipline)
    }

    object jawn {
      lazy val core = "org.typelevel" %% "jawn-parser" % "1.5.1"
    }

  }
}
