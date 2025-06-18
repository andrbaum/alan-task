package com.itv.cacti.db

import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import scala.concurrent.ExecutionContext

object Database {

  def transactor[F[_]: Async](
      host: String,
      port: Int,
      database: String,
      username: String,
      password: String
  ): Resource[F, HikariTransactor[F]] = {
    for {
      connectEc <- ExecutionContexts.fixedThreadPool[F](10)
      xa <- transactor[F](
        host,
        port,
        database,
        username,
        password,
        connectEc
      )
    } yield xa
  }

  protected[db] def transactor[F[_]: Async](
      host: String,
      port: Int,
      database: String,
      username: String,
      password: String,
      connectEc: ExecutionContext
  ): Resource[F, HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](
      driverClassName = "org.postgresql.Driver",
      url = s"jdbc:postgresql://$host:$port/$database",
      user = username,
      pass = password,
      connectEC = connectEc
    )

}
