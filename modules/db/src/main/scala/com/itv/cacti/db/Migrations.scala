package com.itv.cacti.db

import cats.effect.Async
import cats.implicits._
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

object Migrations {

  def run[F[_]: Async](transactor: HikariTransactor[F]) = {
    for {
      flyway <- transactor.configure { dataSource =>
        Async[F].blocking(
          Flyway
            .configure()
            .table("schema_version")
            .dataSource(dataSource)
            .locations("migrations")
            .load()
        )
      }
      _ <- Async[F]
        .blocking(flyway.migrate())

    } yield ()

  }

}
