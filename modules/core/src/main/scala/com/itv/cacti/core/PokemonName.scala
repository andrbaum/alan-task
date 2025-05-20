package com.itv.cacti.core

import io.circe._

final case class PokemonName(name: String)

object PokemonName {

  implicit val abilityEncoder: io.circe.Encoder[PokemonName] = Encoder[String]
    .contramap(_.name)

  implicit val abilityDecoder: io.circe.Decoder[PokemonName] = Decoder[String]
    .map(PokemonName(_))

}
