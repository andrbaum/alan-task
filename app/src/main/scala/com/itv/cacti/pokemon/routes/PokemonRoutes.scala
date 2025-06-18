package com.itv.cacti.pokemon.routes

import cats.Monad
import cats.effect.Concurrent
import cats.implicits._
import java.util.UUID
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.HttpRoutes
import org.http4s.Response
import org.http4s.Status
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import com.itv.cacti.core.Pokemon
import com.itv.cacti.core.PokemonType
import com.itv.cacti.db.PersistenceLayer
import cats.effect.IO

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
        database.getAll.attempt.flatMap {
          case Right(pokemonList) => Ok(pokemonList)
          case Left(e)            =>
            // Log the error
            println(s"Error fetching pokemons: ${e.getMessage}")
            InternalServerError("Database error")
        }
      case GET -> Root / "v1" / "pokemon" / PokemonType(pokemonType) =>
        database
          .getByType(pokemonType)
          .flatMap(pokemonList => Ok(pokemonList))

      case GET -> Root / "v1" / "pokemon" / UUIDVar(uuid) =>
        database
          .getById(uuid)
          .flatMap {
            case Left(_)        => NotFound()
            case Right(pokemon) => Ok(pokemon)
          }

      case req @ POST -> Root / "v1" / "pokemon" =>
        (for {
          pokemon <- req.as[Pokemon]
          id = UUID.randomUUID()
          _        <- database.add(pokemon, id)
          response <- Created(s"Pokemon added with ID: $id")
        } yield response).attempt.flatMap {
          case Right(res) => res.pure[F]
          case Left(e) =>
            // Log the error if needed
            InternalServerError(s"Failed to add Pokemon: ${e.getMessage}")
        }

      case req @ PATCH -> Root / "v1" / "pokemon" / UUIDVar(id) =>
        for {
          pokemon <- req.as[Pokemon]
          response <- database
            .replace(pokemon, id)
            .flatMap {
              case Left(_)  => NotFound()
              case Right(_) => Ok()
            }
        } yield response

      case DELETE -> _ / "v1" / "pokemon" / UUIDVar(id) =>
        database
          .delete(id)
          .flatMap {
            case Left(_)  => NotFound()
            case Right(_) => Ok()
          }

    }

}

object PokemonRoutes {

  def make[F[_]: Concurrent](
      persistenceLayer: PersistenceLayer[F]
  ): PokemonRoutes[F] = new PokemonRoutes[F](persistenceLayer)

}
