package com.itv.cacti.pokemon

import cats.effect.IO
import cats.effect.Resource
import cats.effect.kernel.Async
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes

import com.itv.cacti.db.Database
import com.itv.cacti.db.Migrations
import com.itv.cacti.db.PersistenceLayer
import com.itv.cacti.pokemon.config.AppConfig
import com.itv.cacti.pokemon.config.DatabaseConfig
import com.itv.cacti.pokemon.routes.PokemonRoutes

trait App[F[_]] {
  def http: HttpRoutes[F]
}

object App {

  def apply[F[_]](routes: HttpRoutes[F]): App[F] = {
    new App[F] {
      def http: HttpRoutes[F] = routes
    }
  }

  def mainIO(config: AppConfig): Resource[IO, App[IO]] =
    for {
      xa <- transactor[IO](
        config.databaseConfig
      )
      _          <- Migrations.run(xa).toResource
      repository <- Resource.pure(PersistenceLayer.make[IO](xa))
      routes     <- Resource.pure(PokemonRoutes.make[IO](repository))
      app        <- Resource.pure(App(routes.routes))
    } yield app

  private def transactor[F[_]: Async](
      databaseConfig: DatabaseConfig
  ): Resource[F, HikariTransactor[F]] = {
    for {
      connectEc <- ExecutionContexts.fixedThreadPool[F](10)
      xa <- Database.transactor[F](
        databaseConfig.host,
        databaseConfig.port,
        databaseConfig.database,
        databaseConfig.username,
        databaseConfig.password,
        connectEc
      )
    } yield xa
  }

}
