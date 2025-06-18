package com.itv.cacti.core

case class Secret[A](private val value: A) extends AnyVal {
  override def toString: String = "*******"

  def unravel: A = value
}
