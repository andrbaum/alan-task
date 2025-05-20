package com.itv.cacti.core

import io.circe._

final case class PokemonDescription(description: String)

object PokemonDescription {

  implicit val abilityEncoder: io.circe.Encoder[PokemonDescription] =
    Encoder[String].contramap(_.description)

  implicit val abilityDecoder: io.circe.Decoder[PokemonDescription] =
    Decoder[String].map(PokemonDescription(_))

}
