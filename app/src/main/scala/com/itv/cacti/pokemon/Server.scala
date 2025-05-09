package com.itv.cacti.pokemon

import cats.effect.Resource

object Server {

  def serve[F[_]](
  ) = ???

  /** Task 3
    *
    * You want to create an instance of an Ember Server and return it. Nothing
    * fancy here tbh
    *
    * Interesting bit is the new type Resource!
    *
    * Resource is one of the thing that bring ppl to Scala
    *
    * This type allows you to represent a Resource that can be opened and closed
    * Cool part is that i works AMAZING in concurrent setup
    *
    * Feel free to read more about it here , its interesting topic
    *
    * https://typelevel.org/cats-effect/docs/std/resource
    */

}
