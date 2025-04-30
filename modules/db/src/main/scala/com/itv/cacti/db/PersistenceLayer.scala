package com.itv.cacti.db
import com.itv.cacti.core.Task3.Pokemon

import java.util.UUID

trait PersistenceLayer[F[_]] {


  // TODO - Task 5

  /**
   * Once you model your domian in the Core module this needs to be fixed
   * Delete all the private types and import right representations from Core
   *
   * For example:
   *
   * Type should be enumeration of Pokemon types using enumeratum
   * Pokemon should be a case class representing a Pokemon (name , description , type , level , skills ...)
   * */

  case class PokemonNotFound(errorMessage: String) extends Throwable

  private type HappyPath = String

  private type SadPath = PokemonNotFound

  def getById(id: UUID) : F[Pokemon]

  def getByType(`type`: String): F[List[Pokemon]]

  def getAll: F[List[Pokemon]]

  def add(pokemon: Pokemon, id: Int): F[Unit]
  def replace(pokemon: Pokemon): F[Either[SadPath, HappyPath]]
  def delete(id: UUID): F[Either[SadPath, HappyPath]]
}



object PersistenceLayer {

  def make[F[_]]: PersistenceLayer[F] = new PersistenceLayer[F] {

    /**
     * TODO
     *
     * Using the imported map , implement the following methods
     * Remember that they return and F of return type
     * but the api on Map does not. You will have to explicitly wrap
     * the returns in an abstract F.
     *
     * But how to tell Scala that your F is something that can wrap over something else?
     * Is there a CAPABILITY / TYPECLASS that has this ability ?
     *
     * Hint no.1
     * */

    import com.itv.cacti.db.PokemonUtil.pokemonMap

    override def getById(id: UUID): F[Pokemon] = ???

    override def getByType(`type`: Pokemon): F[List[Pokemon]] = ???

    override def getAll: F[List[Pokemon]] = ???

    override def add(pokemon: Pokemon, id: Int): F[Unit] = ???

    override def replace(pokemon: Pokemon): F[Either[SadPath, HappyPath]] = ???

    override def delete(id: UUID): F[Either[SadPath, HappyPath]] = ???
  }
}


