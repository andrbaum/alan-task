package com.itv.cacti.db
import com.itv.cacti.core.{OperationStatus, Pokemon, PokemonType}
import cats.implicits._

import java.util.UUID
import cats.Applicative


trait PersistenceLayer[F[_]] {

  case class PokemonNotFound(errorMessage: String) extends Throwable

  def getById(id: UUID): F[Either[PokemonNotFound, Pokemon]]

  def getByType(`type`: PokemonType): F[List[Pokemon]]

  def getAll: F[List[(UUID, Pokemon)]]

  def add(pokemon: Pokemon, id:  UUID): F[Unit]
  def replace(
      pokemon: Pokemon,
      uuid: UUID
  ): F[Either[PokemonNotFound, OperationStatus]]
  def delete(id: UUID): F[Either[PokemonNotFound, OperationStatus]]
}

object PersistenceLayer {

  def make[F[_]: Applicative]: PersistenceLayer[F] = new PersistenceLayer[F] {

    import com.itv.cacti.db.PokemonUtil.pokemonMap

    override def getById(id: UUID): F[Either[PokemonNotFound, Pokemon]] = Applicative[F].pure(
      pokemonMap.get(id).toRight(PokemonNotFound("Pokemon not found"))
    )

    override def getByType(`type`: PokemonType): F[List[Pokemon]] =
      Applicative[F].pure(
        pokemonMap.values.filter(_.`type`.contains(`type`)).toList
      )

    override def getAll: F[List[(UUID, Pokemon)]] = Applicative[F].pure(
      pokemonMap.toList
    )

    override def add(pokemon: Pokemon, id:  UUID): F[Unit] = Applicative[F].pure(
      pokemonMap.addOne((id, pokemon))
    )

    override def replace(
        pokemon: Pokemon,
        uuid: UUID
    ): F[Either[PokemonNotFound, OperationStatus]] = {

      Applicative[F].pure{
        Either.fromOption(
          pokemonMap.get(uuid).map(_ => pokemonMap.update(uuid, pokemon)).map(_ => OperationStatus.Replaced),
          PokemonNotFound("Pokemon not found , could not replace")
        )
      }

    }

    override def delete(uuid: UUID): F[Either[PokemonNotFound, OperationStatus]] =
      Applicative[F].pure{
        Either.fromOption(pokemonMap.remove(uuid).map(_ => OperationStatus.Deleted),
          PokemonNotFound("Could not delete, not found") )
      }
  }
}
