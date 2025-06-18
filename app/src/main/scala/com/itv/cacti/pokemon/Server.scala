package com.itv.cacti.pokemon

import cats.effect.Resource
import cats.effect.kernel.Async
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server

import com.itv.cacti.pokemon.config.ServerConfig

object Server {

  def serve[F[_]: Async](
      routes: HttpRoutes[F],
      serverConfig: ServerConfig
  ): Resource[F, server.Server] = {
    EmberServerBuilder.default
      .withPort(serverConfig.port)
      .withHost(serverConfig.host)
      .withHttpApp(routes.orNotFound)
      .build
  }

}
