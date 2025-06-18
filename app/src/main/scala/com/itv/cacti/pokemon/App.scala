package com.itv.cacti.pokemon

import cats.effect.IO
import cats.effect.Resource
import org.http4s.HttpRoutes

import com.itv.cacti.db.Database
import com.itv.cacti.db.Migrations
import com.itv.cacti.db.PersistenceLayer
import com.itv.cacti.pokemon.config.AppConfig
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
      xa <- Database.transactor[IO](
        config.databaseConfig.host,
        config.databaseConfig.port,
        config.databaseConfig.database,
        config.databaseConfig.username,
        config.databaseConfig.password
      )

      _          <- Migrations.run(xa).toResource
      repository <- Resource.pure(PersistenceLayer.make[IO](xa))
      routes     <- Resource.pure(PokemonRoutes.make[IO](repository))
      app        <- Resource.pure(App(routes.routes))
    } yield app

}
