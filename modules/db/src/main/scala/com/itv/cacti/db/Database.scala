package com.itv.cacti.db

import cats.effect.kernel.Async
import cats.effect.kernel.Resource
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import com.itv.cacti.pokemon.config.AppConfig

object Database {

  /** Task 2 - A
    *
    * * in yoda voice * Follow the type signature you must .... Guide it will
    * you... mhmm
    *
    * Note: Finish Small Task 1 to understand all the argument you will need to
    * pass to HikariTransactor constructor
    */

  def transactor[F[_]: Async](
      host: String,
      port: Int,
      database: String,
      username: String,
      password: String
  ): Resource[F, HikariTransactor[F]] = {


}
