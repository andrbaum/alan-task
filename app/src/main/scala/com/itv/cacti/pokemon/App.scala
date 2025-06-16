package com.itv.cacti.pokemon

import cats.effect.IO
import cats.effect.Resource
// import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes

import com.itv.cacti.db.Database
// import com.itv.cacti.db.PersistenceLayer
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

  /** Task 1 - D
    *
    * Config should be a dependency of mainIO function so we want to pass it as
    * an argument so we can use in our constructors
    */

  def mainIO(config: AppConfig): Resource[IO, App[IO]] =
    for {
      db     <- Resource.pure(???)
      routes <- Resource.pure(PokemonRoutes.make[IO](db))
      app    <- Resource.pure(App(routes.routes))
    } yield app

}
