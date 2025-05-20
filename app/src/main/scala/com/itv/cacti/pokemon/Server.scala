package com.itv.cacti.pokemon

import cats.effect.Resource
import cats.effect.kernel.Async
import com.comcast.ip4s._
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server

object Server {

  def serve[F[_]: Async](
      routes: HttpRoutes[F]
  ): Resource[F, server.Server] =
    EmberServerBuilder.default
      .withPort(port"8080")
      .withHost(ipv4"0.0.0.0")
      .withHttpApp(routes.orNotFound)
      .build

}
