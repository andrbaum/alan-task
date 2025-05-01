package com.itv.cacti.core
import io.circe._, io.circe.generic.semiauto._, io.circe.syntax._

final case class PokemonLevel(level: Int)

object PokemonLevel {
  implicit val decoder: io.circe.Decoder[PokemonLevel] = 
    Decoder[Int].map(PokemonLevel(_))
  implicit val encoder: io.circe.Encoder[PokemonLevel] = 
    Encoder[Int].contramap(_.level)

}
