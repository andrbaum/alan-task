package com.itv.cacti.core

import enumeratum.EnumEntry
import enumeratum.Enum

object Task3 {

  /** TODO Task 3
    *
    * Implement a domain model that will represent following JSON
    *
    * { "name" : "Pikachu", "description" : "Electric mouse", "type" :
    * ["Electric"], "level" : 25, "abilities" : [ { "name" : "Electro Shocks",
    * "damage" : 20, "type":["Electric"] } ] }
    *
    * Type should be an enum using enumeratum library
    * https://github.com/lloydmeta/enumeratum
    *
    * Experiment with the style of setting the structure up , you can put
    * everything in package object , or dedicated files , up to you!
    *
    * Compare Catalogue4s com.itv.cit.catalogue.model package with
    * Producers-autha-api com.itv.producers.auth.domain package for better
    * understanding of the approach
    *
    * Make sure that each model has a decoder and encoder !!
    *
    * Explore unwrapped decoders/encoders. They come in handy if you want to
    * avid polluting your json representation with pointless nesting like :
    *
    * case class PokemonName(pokemonName: String) with standard encoder would
    * end up
    *
    * * { "name" : { "pokemonName: "Pikachu" },
    *
    * ...
    *
    * }
    *
    * We dont want that.
    *
    * Once the Task 1 and 2 are Done , the CodecTest object will compile and you
    * will be able to test your model encoder and decoders against it.
    *
    * You also want to create Custom wrappers for Throwable. ( look Task 5 for
    * more contest) The idea is that you want to add more information to the
    * error. So for example if we have a method returning Either[Throwable,
    * Pokemon] we dont know what this throwable is really. We can do the
    * following :
    *
    * case class PokemonNotFound(someAdditionalInfo: String) extends Throwable
    *
    * now this case class fits Left case expectations of Either type so we can
    * return
    *
    * Either[PokemonBotFound, Pokemon] which gives us more information when we
    * look at it so we dont need to look at the implementation of the method at
    * all
    */

  case class Ability(name: String, damage: Int, `type`: PokemonType)

  case class Pokemon(
      name: String,
      `type`: PokemonType,
      description: String,
      level: Int,
      abilities: Seq[Ability] = Seq.empty
  )

  sealed trait PokemonType extends EnumEntry

  object PokemonType extends Enum[PokemonType] {
    case object Electric extends PokemonType
    case object Fire extends PokemonType
    case object Water extends PokemonType
    case object Grass extends PokemonType

    override def values: IndexedSeq[PokemonType] = findValues
  }

  val pikachu = Pokemon(
    "Pikachu",
    PokemonType.Electric,
    "Electric Mouse",
    25,
    Seq(Ability("Electro Shocks", 20, PokemonType.Electric))
  )

}
