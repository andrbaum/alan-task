package com.itv.cacti.core

import cats.effect.IO
import weaver.SimpleIOSuite
import io.circe.literal.JsonStringContext
import io.circe.syntax.EncoderOps
import com.itv.cacti.core.Pokemon

/**
 * TODO - Task 4
 *
 * Split this test in two.
 *
 * We want one case of from Json -> Pokemon with assertion that resulting
 * case class is as expected
 *
 * and another one for Pokemon -> Json with result of expcted json
 *
 * This is covered by this single tes already but it just for you to get familiar
 * with the waver test framework
 *
 * Hint.3
 *
 * */

object CodecTest extends SimpleIOSuite {


  /**
   *  Weaver test is a test framework using cats and cats effect
   *  it lets you define your test cases as an IO and leverage arr the benefits of this type
   *  Check the documentation for more info
   *  but the general idea is that you use test constructor by referring
   *
   *  test(" ... ") { ... }
   *
   *  where { } has a function returning F[Expectations]
   *
   *  where our F is an IO and Expectations is just an assertion wrapped in another
   *  constructor expect(...)
   * */

  test("Json -> Model -> Json") {

    val json =
      json"""
        {
        "name" : "Pikachu",
        "description" : "Electric mouse",
        "type" : ["Electric"],
        "level" : 25,
        "abilities" : [
          {
            "name" : "Electro Shocks",
            "damage" : 20,
            "type":["Electric"]
          }
        ]
      }
          """

   // val expected = Pokemon(...)

    for {
      modelRepresentation <- IO.fromEither(json.as[Pokemon])
      jsonRepresentation <- IO(modelRepresentation.asJson)
    } yield expect( jsonRepresentation == json)

  }

}
