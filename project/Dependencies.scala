package build

import sbt._
import sbt.Keys._

object Dependencies {
  import Modules._

  lazy val rootDeps = Seq(
    cats.core,
    cats.effect
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
    jawn.core,
    http4s.core,
    http4s.dsl,
    http4s.circe,
    http4s.emberServer,
    ciris.core,
    doobie.core,
    doobie.hikari,
    doobie.postgres,
    typesafeConfig.core,
    pureConfig.core,
    flyway.core
  )

  object Modules {

    object flyway {
      val core = "org.flywaydb" % "flyway-core" % "9.22.3"
    }

    object pureConfig {
      val core = "com.github.pureconfig" %% "pureconfig" % "0.17.7"

    }

    object cats {
      val core    = "org.typelevel" %% "cats-core"   % "2.9.0"
      val effect  = "org.typelevel" %% "cats-effect" % "3.4.9"
      val mtl     = "org.typelevel" %% "cats-mtl"    % "1.3.0"
      val kittens = "org.typelevel" %% "kittens"     % "3.0.0"
      val mouse   = "org.typelevel" %% "mouse"       % "1.2.1"
    }

    object circe {
      val core    = "io.circe" %% "circe-core"    % "0.14.3"
      val generic = "io.circe" %% "circe-generic" % "0.14.3"
      val literal = "io.circe" %% "circe-literal" % "0.14.3"
    }

    object http4s {
      val org = "org.http4s"
      val v   = "0.23.26"

      val core        = org %% "http4s-core"         % v
      val dsl         = org %% "http4s-dsl"          % v
      val emberServer = org %% "http4s-ember-server" % v
      val emberClient = org %% "http4s-ember-client" % v
      val circe       = org %% "http4s-circe"        % v
    }

    object enumeratum {
      val v = "1.7.2"

      val core   = "com.beachape" %% "enumeratum"        % v
      val doobie = "com.beachape" %% "enumeratum-doobie" % "1.7.5"
      val circe  = "com.beachape" %% "enumeratum-circe"  % v

    }

    object weaver {
      private lazy val org     = "com.disneystreaming"
      private lazy val version = "0.8.1"

      lazy val cats       = org %% "weaver-cats"       % version % Test
      lazy val scalacheck = org %% "weaver-scalacheck" % version % Test
      lazy val discipline = org %% "weaver-discipline" % version % Test

      lazy val test = Seq(cats, scalacheck, discipline)
    }

    object jawn {
      lazy val core = "org.typelevel" %% "jawn-parser" % "1.5.1"
    }

    object ciris {
      val core = "is.cir" %% "ciris" % "3.8.0"
    }

    object doobie {
      val core     = "org.tpolecat" %% "doobie-core"     % "1.0.0-RC8"
      val hikari   = "org.tpolecat" %% "doobie-hikari"   % "1.0.0-RC8"
      val postgres = "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC8"
    }

    object typesafeConfig {
      val core = "com.typesafe" % "config" % "1.4.3"
    }

  }

}
