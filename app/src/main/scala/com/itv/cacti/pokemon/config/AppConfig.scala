package com.itv.cacti.pokemon.config

import cats.Applicative
import com.typesafe.config.ConfigFactory

case class AppConfig(
    host: String,
    port: Int,
    database: String,
    username: String,
    password: String
)

object AppConfig {

  def Load[F[_]: Applicative]: F[AppConfig] = {
    val config           = ConfigFactory.load()
    val host: String     = config.getString("app.host")
    val port: Int        = config.getInt("app.port")
    val database: String = config.getString("app.database")
    val username: String = config.getString("app.username")
    val password: String = config.getString("app.password")

    Applicative[F].pure {
      AppConfig(host, port, database, username, password)
    }
  }

}
