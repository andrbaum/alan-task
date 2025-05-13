package com.itv.cacti.pokemon

import cats.effect.{IO, Resource}
import org.http4s.HttpRoutes
import com.itv.cacti.db.PersistenceLayer

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
    /** Task 4
      *
      * We need to create instance of an App wrapped in Resource that works in
      * IO context App is the class representing everything our App has to offer
      * , in this case its only Http4s routes
      *
      * Your goal is to:
      *
      *   - create instance of Persistence layer wrapped in Resource
      *   - create instance of PokemonRoutes wrapped in resource
      *   - Use routes from PokemonRoutes to create instance of App
      *
      * use for comprehension for that!
      *
      * Try to answer the question : Do you need to wrap instance of App in
      * Resource? E
      */

    for {
      persistence <- IO[Resource[PersistenceLayer]]

    } yield persistence

}
