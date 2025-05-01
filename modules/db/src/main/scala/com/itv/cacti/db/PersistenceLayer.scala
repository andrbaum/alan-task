package com.itv.cacti.db
import com.itv.cacti.core.Pokemon

import java.util.UUID
import cats.Applicative
import cats.effect._
import com.itv.cacti.core.PokemonType

trait PersistenceLayer[F[_]] {

  // TODO - Task 5

  /** Once you model your domian in the Core module this needs to be fixed
    * Delete all the private types and import right representations from Core
    *
    * For example:
    *
    * Type should be enumeration of Pokemon types using enumeratum Pokemon
    * should be a case class representing a Pokemon (name , description , type ,
    * level , skills ...)
    */

  case class PokemonNotFound(errorMessage: String) extends Throwable

  def getById(id: UUID): F[Pokemon]

  def getByType(`type`: PokemonType): F[List[Pokemon]]

  def getAll: F[List[Pokemon]]

  def add(pokemon: Pokemon, id: Int): F[Unit]
  def replace(
      pokemon: Pokemon,
      uuid: UUID
  ): F[Either[PokemonNotFound, Map[UUID, Pokemon]]]
  def delete(id: UUID): F[Either[PokemonNotFound, String]]
}

object PersistenceLayer {

  def make[F[_]: Applicative]: PersistenceLayer[F] = new PersistenceLayer[F] {

    /** TODO
      *
      * Using the imported map , implement the following methods Remember that
      * they return and F of return type but the api on Map does not. You will
      * have to explicitly wrap the returns in an abstract F.
      *
      * But how to tell Scala that your F is something that can wrap over
      * something else? Is there a CAPABILITY / TYPECLASS that has this ability
      * ?
      *
      * Hint no.1
      */

    import com.itv.cacti.db.PokemonUtil.pokemonMap

    override def getById(id: UUID): F[Pokemon] = Applicative[F].pure(
      pokemonMap.get(id) match {
        case Some(pokemon) => pokemon
        case None          => throw PokemonNotFound("Pokemon not found")
      }
    )

    override def getByType(`type`: PokemonType): F[List[Pokemon]] =
      Applicative[F].pure(
        pokemonMap.values.filter(_.`type`.contains(`type`)).toList
      )

    override def getAll: F[List[Pokemon]] = Applicative[F].pure(
      pokemonMap.values.toList
    )

    override def add(pokemon: Pokemon, id: Int): F[Unit] = Applicative[F].pure(
      pokemonMap.updated(UUID.randomUUID(), pokemon)
    )

    override def replace(
        pokemon: Pokemon,
        uuid: UUID
    ): F[Either[PokemonNotFound, Map[UUID, Pokemon]]] =
      Applicative[F].pure(
        pokemonMap.get(uuid) match {
          case Some(_) =>
            pokemonMap.updated(uuid, pokemon)
            Right(pokemonMap)
          case None => Left(PokemonNotFound("Pokemon not found"))
        }
      )

    override def delete(uuid: UUID): F[Either[PokemonNotFound, String]] =
      pokemonMap.get(uuid) match {
        case Some(_) =>
          Applicative[F].pure(Right("Deleted successfully"))
        case None =>
          Applicative[F].pure(Left(PokemonNotFound("Pokemon not found")))
      }
  }
}
