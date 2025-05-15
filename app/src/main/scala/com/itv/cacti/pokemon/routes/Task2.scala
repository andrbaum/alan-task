package com.itv.cacti.pokemon.routes

import io.circe._, io.circe.generic.semiauto._, io.circe.syntax._
import io.circe._, io.circe.generic.semiauto._, io.circe.syntax._
import cats.implicits._
import cats.effect.IO
import cats.effect.Concurrent
import cats.Applicative
import com.itv.cacti.db.PersistenceLayer
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.syntax.applicative
import cats.Monad
import cats._
import com.itv.cacti.core.Pokemon
import java.util.UUID
import org.http4s.circe._
import org.http4s.EntityEncoder
import com.itv.cacti.core.PokemonType
import org.http4s.Response
import org.http4s.Status
import org.http4s.EntityDecoder
import com.itv.cacti.core.PokemonLevel.decoder
import com.itv.cacti.core.OperationStatus

object Task2 {

  /** Task 2
    *
    * So now we have out project set up and the persistence layer existing we
    * can use it for some good
    *
    * Your goals is to define a new class that will represents the routes server
    * by our api The set up is the same as for the most situations
    *
    *   - you want to define final class that will operate in context of some
    *     abstract F Hint.1
    *   - define the class dependencies ( persistence layer in this case )
    *   - Bring Http4sDsl for the given F to the scope Hint.2
    *   - define companion object with make function that returns an instance of
    *     PokemonRoutes
    *
    * Side note : the companion object might seem redundant , and it kind of is
    * at this point but we will use this set up later
    *
    * Pay particular focus on impliocits! You will need a lot of them for the
    * whole thing to work
    *
    * Good thing imo is to start with always having
    *
    * import cats.implicits._
    *
    * present. That's where the power of cats comes from. I will let you figure
    * out other ones as part of the exercise
    *
    * REMEMBER ABOUT THE CONTEXT BOUNDS OF THE F !!
    *
    * Routes:
    *
    * GET /v1/pokemon - returning List of all Pokemons in the resistance layer
    * GET /v1/pokemon/{type} - Returning list of all Pokemons of given type (
    * empty list if none) GET /v1/pokemon/{id} - Return Option of Pokemon by id
    * (None if ID missing in db )
    *
    * POST /v1/pokemon - Adds new pokemon to the database PATCH /v1/pokemon/{id}
    * \- Update Pokemon info by id (404 if wrong id) DELETE /v1/pokemon/{id} -
    * deletes Pokemon by id ( 404 if wrong id )
    *
    * You also would like to be able to decode the values from the URI path
    * itself this way you will be able to get for example PokemonType out of the
    * URI , rather than hawing a string and trying to get PokeMonType out of it
    *
    * Hint 3
    *
    * Http4s documentation is your best friend here , they have really good
    * examples.
    *
    * https://http4s.org/v0.23/docs/quickstart.html
    */

  final class PokemonRoutes[F[_]: Concurrent](
      database: PersistenceLayer[F]
  ) extends Http4sDsl[F] {

    implicit val pokemonListEncoder: EntityEncoder[F, List[Pokemon]] =
      jsonEncoderOf[F, List[Pokemon]]

    implicit val pokemonListWithUUIDEncoder: EntityEncoder[F, List[
      (UUID, Pokemon)
    ]] = jsonEncoderOf[F, List[(UUID, Pokemon)]]

    implicit val pokemonEncoder: EntityEncoder[F, Pokemon] =
      jsonEncoderOf[F, Pokemon]

    implicit val pokemonDecoder: EntityDecoder[F, Pokemon] = jsonOf[F, Pokemon]

    def routes: HttpRoutes[F] =
      HttpRoutes.of[F] {
        case GET -> Root / "v1" / "pokemon" =>
          database.getAll.flatMap(pokemonList =>
            Monad[F].pure(
              Response[F](
                status = Status.Ok,
                body = fs2.Stream(
                  pokemonList
                    .map(_.toString().toByte)
                    .reduce((a, b) => a.combine(b))
                )
              )
            )
          )
        case GET -> Root / "v1" / "pokemon" / PokemonType(pokemonType) =>
          database
            .getByType(pokemonType)
            .flatMap(pokemonList => Ok(pokemonList))

        case GET -> Root / "v1" / "pokemon" / UUIDVar(uuid) =>
          database
            .getById(uuid)
            .flatMap(response =>
              response match {
                case Left(_)        => NotFound()
                case Right(pokemon) => Ok(pokemon)
              }
            )

        case req @ POST -> Root / "v1" / "pokemon" =>
          for {
            pokemon <- req.as[Pokemon]
            id = UUID.randomUUID()
            _        <- database.add(pokemon, id)
            response <- Created(s"Pokemon added with ID: $id")
          } yield response

        case req @ PATCH -> Root / "v1" / "pokemon" / UUIDVar(id) =>
          for {
            pokemon <- req.as[Pokemon]
            response <- database
              .replace(pokemon, id)
              .flatMap(response =>
                response match {
                  case Left(_)  => NotFound()
                  case Right(_) => Ok()
                }
              )
          } yield response

        case DELETE -> root / "v1" / "pokemon" / UUIDVar(id) =>
          database
            .delete(id)
            .flatMap(response =>
              response match {
                case Left(_)  => NotFound()
                case Right(_) => Ok()
              }
            )

      }

  }

  object PokemonRoutes {

    def make[F[_]: Concurrent](
        persistenceLayer: PersistenceLayer[F]
    ): PokemonRoutes[F] = new PokemonRoutes[F](persistenceLayer)

  }

}
