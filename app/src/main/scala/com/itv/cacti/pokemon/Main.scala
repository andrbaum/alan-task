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

    val server: Resource[IO, Server] =
      for {
        conf   <- AppConfig.Load[IO].toResource
        app    <- App.mainIO(conf)
        server <- Server.serve[IO](app.http, conf.serverConfig)
      } yield server

    val serverStream = Stream.resource(server).evalMap(_ => IO.never)

    serverStream
      .as(ExitCode.Success)
      .compile
      .lastOrError

  }

}
