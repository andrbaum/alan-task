package com.itv.cacti.core

import enumeratum.CirceEnum
import enumeratum.Enum
import enumeratum.EnumEntry

sealed trait PokemonType extends EnumEntry

object PokemonType extends Enum[PokemonType] with CirceEnum[PokemonType] {
  case object Electric extends PokemonType
  case object Fire     extends PokemonType
  case object Water    extends PokemonType
  case object Grass    extends PokemonType
  case object Poison   extends PokemonType
  case object Normal   extends PokemonType
  case object Fairy    extends PokemonType
  case object Fighting extends PokemonType
  case object Psychic  extends PokemonType

  override def values: IndexedSeq[PokemonType] = findValues

  def unapply(maybePokemonType: String): Option[PokemonType] =
    PokemonType.withNameOption(maybePokemonType)

}
