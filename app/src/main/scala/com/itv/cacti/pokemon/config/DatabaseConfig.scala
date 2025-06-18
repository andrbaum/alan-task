package com.itv.cacti.pokemon.config

import com.comcast.ip4s.Host
import com.comcast.ip4s.Port

import com.itv.cacti.core.Secret

case class DatabaseConfig(
    database: String,
    username: String,
    password: Secret[String],
    host: Host,
    port: Port
)
