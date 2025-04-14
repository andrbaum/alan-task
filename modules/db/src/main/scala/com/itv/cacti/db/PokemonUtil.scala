package com.itv.cacti.db

import java.util.UUID

object PokemonUtil {

  /**
   * Example Pokemon map.
   *
   * feel free to change it as you wish.
   *
   * Note that if you will use wrappers for the member values ( and you should! )
   * and use enums for pokemon type
   * this will look more like
   * Pokemon(PokemonName("Pikachu"), Description("Electric mouse"), PokemonType.ElECTRIC, PokemonLevel(25) , etc ...)
   * */
  val pokemonMap: Map[UUID, Pokemon] = Map(
    UUID.randomUUID() -> Pokemon("Pikachu", "Electric mouse",List( "Electric"), 25,  Ability("Electro Shock", 20, List("Electric"))),
    UUID.randomUUID() -> Pokemon("Bulbasaur", "Weird turtle",List("Grass", "Poison"), 15,  Ability("Leaf throwing thingy ", 20, List("Grass"))),
    UUID.randomUUID() -> Pokemon("Charmander","Walking angry lighter",List("Fire"), 18, Ability("Burn your enemies and their homes", 999, List("Fire"))),
    UUID.randomUUID() -> Pokemon("Jigglypuff","Lil Graffiti artist", List("Normal", "Fairy"), 20, Ability("Yawn ... i am sleeepy", 0, List("Fairy"))),
    UUID.randomUUID() -> Pokemon("Meowth", "Crazy cat lady favorite", List("Normal"), 22, Ability("Fur ball on the carpet", 999, List("Normal"))),
    UUID.randomUUID() -> Pokemon("Machop",  "Do you even lift brah?", List("Fighting"), 19, Ability("Karate chop", 50, List("Figthing")))
  )



}
