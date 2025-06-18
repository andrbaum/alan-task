package com.itv.cacti.db

import cats.implicits._
import doobie.hikari.HikariTransactor
import java.util.UUID

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

object PersistenceLayer {

  def make[F[_]: cats.effect.kernel.MonadCancelThrow](
      transactor: HikariTransactor[F]
  ): PersistenceLayer[F] =
    new PersistenceLayer[F] {

      import doobie.implicits._
      import doobie.postgres.implicits._

      def getById(id: UUID): F[Either[PokemonNotFound, Pokemon]] = ???

      def getByType(`type`: PokemonType): F[List[Pokemon]] = ???

      def getAll: F[List[(UUID, Pokemon)]] = {
        sql"SELECT id, name, description, type, level, abilities FROM pokemon"
          .query[
            (UUID, String, String, List[String], Int, List[String])
          ] // adjust types as needed
          .to[List]
          .map(_.map { case (id, name, desc, types, level, abilities) =>
            // You need to parse typ and abilities appropriately here
            val pokemon = Pokemon(
              PokemonName(name),
              PokemonDescription(desc),
              types.map(PokemonType.withName),
              PokemonLevel(level),
              abilities.map(a => Ability(a, 0, List.empty)) // Adjust as needed
            )
            (id, pokemon)
          })
          .transact(transactor)
      }

      def add(pokemon: Pokemon, id: UUID): F[Unit] = {
        val sqlInsert =
          sql"""
        INSERT INTO pokemon (id, name, description, type, level, abilities)
        VALUES ($id, ${pokemon.name}, ${pokemon.description}, 
          ${pokemon.`type`.map(_.entryName)}, ${pokemon.level}, 
          ${pokemon.abilities.map(a => a.name)})
        """.update.run
        sqlInsert.transact(transactor).void
      }

      def replace(
          pokemon: Pokemon,
          uuid: UUID
      ): F[Either[PokemonNotFound, OperationStatus]] = ???

      def delete(id: UUID): F[Either[PokemonNotFound, OperationStatus]] = ???
    }

}
