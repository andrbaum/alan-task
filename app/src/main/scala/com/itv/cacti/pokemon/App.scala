package com.itv.cacti.pokemon

import cats.effect.IO
import cats.effect.Resource
import org.http4s.HttpRoutes

import com.itv.cacti.db.PersistenceLayer
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

  def mainIO(): Resource[IO, App[IO]] =
    for {
      db     <- Resource.pure(PersistenceLayer.make[IO])
      routes <- Resource.pure(PokemonRoutes.make[IO](db))
      app    <- Resource.pure(App(routes.routes))
    } yield app

}
