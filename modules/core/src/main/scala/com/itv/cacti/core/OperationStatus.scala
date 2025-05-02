package com.itv.cacti.core

import enumeratum.{CirceEnum, EnumEntry}
import enumeratum.Enum


// This is just to represent the outcome of the computation , so we have something meaningfully to return

sealed trait OperationStatus extends EnumEntry

object OperationStatus extends Enum[OperationStatus] with CirceEnum[OperationStatus] {
  case object Replaced extends OperationStatus

  case object Deleted extends OperationStatus

  override def values: IndexedSeq[OperationStatus] = findValues
}
