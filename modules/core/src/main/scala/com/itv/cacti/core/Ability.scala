package com.itv.cacti.core

import io.circe._, io.circe.generic.semiauto._, io.circe.syntax._
import com.itv.cacti.core.PokemonType

final case class Ability(name: String, damage: Int, `type`: List[PokemonType])

object Ability {
  implicit val abilityEncoder: io.circe.Encoder[Ability] = deriveEncoder

  implicit val abilityDecoder: io.circe.Decoder[Ability] = deriveDecoder
}
