package com.itv.cacti.pokemon.config

import com.typesafe.config.ConfigFactory

class AppConfig {
  private val config   = ConfigFactory.load()
  val host: String     = config.getString("app.host")
  val port: Int        = config.getInt("app.port")
  val database: String = config.getString("app.database")
  val username: String = config.getString("app.username")
  val password: String = config.getString("app.password")
}
