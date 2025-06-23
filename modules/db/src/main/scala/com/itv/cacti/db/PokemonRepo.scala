package com.itv.cacti.db

import cats.effect.MonadCancelThrow
import doobie.hikari.HikariTransactor
import java.util.UUID

import com.itv.cacti.core._
import com.itv.cacti.db.PokemonSql.getPokemonById
import com.itv.cacti.db.PokemonSql.getPokemonAbilitiesByPokemonId
import com.itv.cacti.db.PokemonSql.PokemonCoreInfo
import com.itv.cacti.db.PokemonSql.PokemonAbilityInfo

trait PokemonRepo[F[_]] {

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

object PokemonRepo {

  def make[F[_]: MonadCancelThrow](
      transactor: HikariTransactor[F]
  ): PokemonRepo[F] =
    new PokemonRepo[F] {

      def getById(id: UUID): F[Either[PokemonNotFound, Pokemon]] = {
        val pokemon: Pokemon =
          for {
            pokemon <- getPokemonById(id)
            pokemonAbilities <-
              getPokemonAbilitiesByPokemonId[PokemonAbilityInfo](id)
            abilties <- getPokemonAbilitiesByAbilityId(
              pokemonAbilities.abilityId
            )
          } yield Pokemon(pokemon)
      }

      /**   1. Get the Pokemon basic info per uuid from Pokemon table - If
        *      missing , then PokemonNotFound 2. Get all ability ID for that
        *      UUID (pokemon_id) from pokemon_ability table 3. Get all ability
        *      type links for each ability_id from query above 4. Get all types
        *      for pokemon_ID from pokemon_type_link
        */

      def getByType(`type`: PokemonType): F[List[Pokemon]] = ???

      /**   1. Get All Pokemon_Id for given type from Pokemon_type_link table 2.
        *      For each Pokemon_id from query above :
        *      - Get the Pokemon basic info per uuid from pokemon table
        *      - Get all ability ID for each UUID (pokemon_id) from
        *        pokemon_ability table
        *      - Get all ability type links for each ability_id from query above
        */

      def getAll: F[List[(UUID, Pokemon)]] = ???

      /**   1. Get all Pokemon basic info from Pokemon table and their UUID's 2.
        *      For each UUID from query above :
        *      - Get all ability ID for that UUID (pokemon_id) from
        *        pokemon_ability table
        *      - Get all ability type links for each ability_id from query above
        *      - Get all types for pokemon_ID from pokemon_type_link
        */

      def add(pokemon: Pokemon, id: UUID): F[Unit] = ???

      /**   1. Insert relevant data into pokemon table 2. Link pokemon to
        *      type/types pokemon_type_link 3. Insert relevant data into ability
        *      table 4. Link ability with type in ability_type_link 5. Link
        *      Pokemon with Ability in pokemon_ability
        *
        * NOTE 1: We get UUID for pokemon ID beforehand , but not for ability id
        * we will need to create new UUID as part of the operation
        *
        * NOTE 2: We might find ourself in a situation when the Pokemon Type or
        * ability already exist we dont want to have duplicates in our db and
        * postgres will block you from creating something that does already by
        * throwing an duplicate key value error, we can go around it by using ON
        * CONFLICT DO NOTHING in our insert query to avoid that from happening
        * and just accept the fact. We will implement idempotent http calls
        * later to avoid getting to this point
        */

      def replace(
          pokemon: Pokemon,
          uuid: UUID
      ): F[Either[PokemonNotFound, OperationStatus]] = ???

      /** Run delete operation Run insert operation right after
        */

      def delete(id: UUID): F[Either[PokemonNotFound, OperationStatus]] = ???

      /**   1. Delete entry from pokemon table 2. delete entries from
        *      pokemon_type_link 3. Delete entries from pokemon_ability
        */

    }

}
