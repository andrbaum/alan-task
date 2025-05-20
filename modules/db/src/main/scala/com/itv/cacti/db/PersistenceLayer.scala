package com.itv.cacti.db

import cats.Applicative
import cats.implicits._
import java.util.UUID
import scala.collection.mutable

import com.itv.cacti.core._

trait PersistenceLayer[F[_]] {

  case class PokemonNotFound(errorMessage: String) extends Throwable

  def getById(id: UUID): F[Either[PokemonNotFound, Pokemon]]

  def getByType(`type`: PokemonType): F[List[Pokemon]]

  def getAll: F[List[(UUID, Pokemon)]]

  def add(pokemon: Pokemon, id: UUID): F[Unit]

  def replace(
      pokemon: Pokemon,
      uuid: UUID
  ): F[Either[PokemonNotFound, OperationStatus]]

  def delete(id: UUID): F[Either[PokemonNotFound, OperationStatus]]
}

/** This is redundant as we will be using our postgreSQL db instead.
  *
  * We cna still use it for our tests as a stub pokemon DB so lets move it
  * there!
  *
  * Implementation of PersistenceLayer below should be used as
  * StubPersistenceLayer and put together with our routes tests
  *
  * Same goes to Pokemon Util which got the actual db.
  */
object PersistenceLayer {

  def make[F[_]: Applicative]: PersistenceLayer[F] =
    new PersistenceLayer[F] {

      val pokemonMap: mutable.Map[UUID, Pokemon] = mutable.Map(
        UUID.randomUUID() -> Pokemon(
          PokemonName("Pikachu"),
          PokemonDescription("Electric mouse"),
          List(PokemonType.Electric),
          PokemonLevel(25),
          List(Ability("Electro Shock", 20, List(PokemonType.Electric)))
        ),
        UUID.randomUUID() -> Pokemon(
          PokemonName("Bulbasaur"),
          PokemonDescription("Weird turtle"),
          List(PokemonType.Grass, PokemonType.Poison),
          PokemonLevel(15),
          List(Ability("Leaf throwing thingy ", 20, List(PokemonType.Grass)))
        ),
        UUID.randomUUID() -> Pokemon(
          PokemonName("Charmander"),
          PokemonDescription("Walking angry lighter"),
          List(PokemonType.Fire),
          PokemonLevel(18),
          List(
            Ability(
              "Burn your enemies and their homes",
              999,
              List(PokemonType.Fire)
            )
          )
        ),
        UUID.randomUUID() -> Pokemon(
          PokemonName("Jigglypuff"),
          PokemonDescription("Lil Graffiti artist"),
          List(PokemonType.Normal, PokemonType.Fairy),
          PokemonLevel(20),
          List(Ability("Yawn ... i am sleeepy", 0, List(PokemonType.Fairy)))
        ),
        UUID.randomUUID() -> Pokemon(
          PokemonName("Meowth"),
          PokemonDescription("Crazy cat lady favorite"),
          List(PokemonType.Normal),
          PokemonLevel(22),
          List(Ability("Fur ball on the carpet", 999, List(PokemonType.Normal)))
        ),
        UUID.randomUUID() -> Pokemon(
          PokemonName("Machop"),
          PokemonDescription("Do you even lift brah?"),
          List(PokemonType.Fighting),
          PokemonLevel(19),
          List(Ability("Karate chop", 50, List(PokemonType.Fighting)))
        )
      )

      override def getById(id: UUID): F[Either[PokemonNotFound, Pokemon]] =
        Applicative[F].pure(
          pokemonMap.get(id).toRight(PokemonNotFound("Pokemon not found"))
        )

      override def getByType(`type`: PokemonType): F[List[Pokemon]] =
        Applicative[F].pure(
          pokemonMap.values.filter(_.`type`.contains(`type`)).toList
        )

      override def getAll: F[List[(UUID, Pokemon)]] =
        Applicative[F].pure(
          pokemonMap.toList
        )

      override def add(pokemon: Pokemon, id: UUID): F[Unit] =
        Applicative[F].pure(
          pokemonMap.addOne((id, pokemon))
        )

      override def replace(
          pokemon: Pokemon,
          uuid: UUID
      ): F[Either[PokemonNotFound, OperationStatus]] = {

        Applicative[F].pure {
          Either.fromOption(
            pokemonMap
              .get(uuid)
              .map(_ => pokemonMap.update(uuid, pokemon))
              .map(_ => OperationStatus.Replaced),
            PokemonNotFound("Pokemon not found , could not replace")
          )
        }

      }

      override def delete(
          uuid: UUID
      ): F[Either[PokemonNotFound, OperationStatus]] =
        Applicative[F].pure {
          Either.fromOption(
            pokemonMap.remove(uuid).map(_ => OperationStatus.Deleted),
            PokemonNotFound("Could not delete, not found")
          )
        }

    }

}
