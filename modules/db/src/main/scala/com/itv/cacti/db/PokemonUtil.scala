package com.itv.cacti.db

import java.util.UUID
import com.itv.cacti.core.Pokemon
import com.itv.cacti.core.PokemonType
import com.itv.cacti.core.Ability
import com.itv.cacti.core.PokemonName
import com.itv.cacti.core.PokemonDescription
import com.itv.cacti.core.PokemonLevel

import scala.collection.mutable

object PokemonUtil {

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
}
