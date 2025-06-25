package com.itv.cacti.db

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import java.util.UUID

import com.itv.cacti.core.PokemonDescription
import com.itv.cacti.core.PokemonLevel
import com.itv.cacti.core.PokemonName
import com.itv.cacti.core.PokemonType

object PokemonSql {

  /** *
    *
    * Your task is to define all of the SQL interactions in order to retrieve
    * what we need. The expected composition is defined for you in PokemonRepo (
    * used to be called persistenceLayer)
    *
    * The main goal of this task is to lear about writing queries and most
    * important practice composition! STRONG emphasis on the latter.
    *
    * What I mean by that?
    *
    * Instead of writing one chunky query to get the things we need I would like
    * you to try to create a lot of small queries and try to compose them in
    * order to get what we want. For example:
    *
    * def getById(id: UUID): F[Either[PokemonNotFound, Pokemon]] = ???
    *
    *   1. Get the Pokemon basic info per uuid from Pokemon table - If missing ,
    *      then PokemonNotFound 2. Get all ability ID for that UUID (pokemon_id)
    *      from pokemon_ability table 3. Get all ability type links for each
    *      ability_id from query above 4. Get all types for pokemon_ID from
    *      pokemon_type_link
    *
    * We can first write a query that will return information from the pokemon
    * table and that is: id : uuid name : varchar description: text level: int
    *
    * We can create intermediate case class for that so our query will return
    * PokemonCoreInfo instead of primitives!
    */

  case class PokemonCoreInfo(
      id: UUID,
      name: PokemonName,
      description: PokemonDescription,
      level: PokemonLevel
  )

  case class PokemonAbilityInfo(
      pokemonId: UUID,
      abilityId: UUID
  )

  case class AbilityInfo(
      id: UUID,
      name: String,
      damage: Int
  )

  def getPokemonById(pokemonId: UUID): ConnectionIO[Option[PokemonCoreInfo]] = {
    sql"""
    SELECT id, name, description, level
    FROM pokemon
    WHERE id = $pokemonId
  """
      .query[PokemonCoreInfo]
      .option

  }

  def getPokemonAbilitiesByPokemonId(
      pokemonId: UUID
  ): ConnectionIO[List[PokemonAbilityInfo]] = {
    sql"""
    SELECT ability_id
    FROM pokemon_ability
    WHERE pokemon_id = $pokemonId
    """
      .query[PokemonAbilityInfo]
      .to[List]
  }

  def getPokemonAbilitiesByAbilityId(
      abilityIds: List[UUID]
  ): ConnectionIO[List[AbilityInfo]] = {
    sql"""
    SELECT id, name, damage
    FROM pokemon_ability
    WHERE id IN (${abilityIds.mkString(",")})
    """
      .query[AbilityInfo]
      .to[List]
  }

  def getPokemonTypeByPokemonId(
      pokemonId: UUID
  ): ConnectionIO[List[PokemonType]] = {
    sql"""
    SELECT type
    FROM pokemon_type_link
    WHERE pokemon_id = $pokemonId
    """
      .query[PokemonType]
      .to[List]
  }

  def getPokemonTypeByAbilityId(
      abilityId: UUID
  ): ConnectionIO[List[PokemonType]] = {
    sql"""
    SELECT type
    FROM ability_type_link
    WHERE abilityId = $abilityId
    """
      .query[PokemonType]
      .to[List]
  }


  /** So we have small query that returns information from one table only and
    * queries it directly into our case class using .query method
    *
    * but how does that work an why?
    *
    * The answer is that the query function requires some sort of implicit to be
    * somewhere in the scope that will provide the necessary logic to make this
    * happen and that's called Meta[T] where T is the type we want to be able to
    * read.
    *
    * So in this case we need Meta[PokemonCoreInfo], but is that all ? Nope! We
    * also need Meta for the member case classes as they are not primitive so we
    * need:
    *
    * Meta[PokemonName] which is essentially a wrapper over a String
    * Meta[PokemonDescription] same Meta[PokemonLevel] wrapper over an Int
    * Meta[UUID] Comes out of the box with Doobie! import
    * doobie.postgres.implicits._
    *
    * Given that we have a Meta for all the members we can automatically derive
    * Meta for PokemonCoreInfo! So actually we dont even need explicit
    * Meta[PokemonCoreInfo] , Scala will figure this out on its own!
    */

  /**   1. Get the Pokemon basic info per uuid from Pokemon table - If missing ,
    *      then PokemonNotFound
    *
    * The function we wrote above gives us ConnectionIO[Option[PokemonCoreInfo]]
    * ConnectionIO is a Monad so we can map and flatMap over it , so we can
    * combine them! We can create multiple ConnectionIO , combine them into one
    * and run that to get our composed query
    *
    * If we want to this query we call .transact method on ConnectionIO , pass
    * HikariTransactor as an argument and we are DONE!
    *
    * Now lets look into 2 . Get all ability ID for that UUID (pokemon_id) from
    * pokemon_ability table
    *
    * And that where you take the wheel! We will stay in the db module for now.
    * The intermediate case classes are only relevant in the db module scope ,
    * feel free to create another package ( or package object if you don't want
    * to split each case class into separate file ) called model in this module.
    * Look at core module for inspiration if needed.
    *
    * Given that we will need Meta for each Wrapper in Pokemon case class we
    * need to put implicits somewhere right? But we don't want to add Doobie
    * dependency in our Core Module ( that where Pokemon case class and wrappers
    * live ) so we need to come up with better placement to avoid overloading
    * Core module with dependencies!
    */

}
