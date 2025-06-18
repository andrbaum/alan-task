package com.itv.cacti.pokemon.config

import cats.Applicative
import cats.syntax.applicative._
import pureconfig._
import pureconfig.generic.auto._

case class AppConfig(
    databaseConfig: DatabaseConfig,
    serverConfig: ServerConfig
)

object AppConfig {

  def Load[F[_]: Applicative]: F[AppConfig] = {
    ConfigSource.default.load[AppConfig] match {
      case Right(config) => config.pure[F]
      case Left(errors) =>
        throw new RuntimeException(
          s"Configuration loading failed: ${errors.toList.mkString(", ")}"
        )
    }
  }

}
