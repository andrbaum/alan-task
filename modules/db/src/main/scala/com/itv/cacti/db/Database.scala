package com.itv.cacti.db

import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import com.comcast.ip4s.Host
import com.comcast.ip4s.Port
import doobie.hikari.HikariTransactor
import scala.concurrent.ExecutionContext

import com.itv.cacti.core.Secret

object Database {

  def transactor[F[_]: Async](
      host: Host,
      port: Port,
      database: String,
      username: String,
      password: Secret[String],
      connectEc: ExecutionContext
  ): Resource[F, HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](
      driverClassName = "org.postgresql.Driver",
      url = s"jdbc:postgresql://$host:$port/$database",
      user = username,
      pass = password.unravel,
      connectEC = connectEc
    )

}
