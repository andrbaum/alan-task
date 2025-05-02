package com.itv.cacti.core
import io.circe._, io.circe.generic.semiauto._, io.circe.syntax._
import com.itv.cacti.core.PokemonType

case class Pokemon(
    name: PokemonName,
    description: PokemonDescription,
    `type`: List[PokemonType],
    level: PokemonLevel,
    abilities: List[Ability] = List.empty
)

object Pokemon {

  implicit val pokemonEncoder: io.circe.Encoder[Pokemon] =
    deriveEncoder

  implicit val pokemonDecoder: io.circe.Decoder[Pokemon] =
    deriveDecoder
}
