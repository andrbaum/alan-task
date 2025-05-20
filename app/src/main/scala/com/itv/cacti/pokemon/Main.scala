package com.itv.cacti.pokemon

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import fs2.Stream
import org.http4s.server.Server

import com.itv.cacti.pokemon.config.AppConfig

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    /** Task 1 - E
      *
      * Given we have our Config defined we need to load it and pass in the
      * right place
      */

    val server: Resource[IO, Server] =
      for {
        app    <- App.mainIO()
        server <- Server.serve[IO](app.http)
      } yield server

    val serverStream = Stream.resource(server).evalMap(_ => IO.never)

    serverStream
      .as(ExitCode.Success)
      .compile
      .lastOrError

  }

}
