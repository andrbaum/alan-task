package com.itv.cacti.core

import cats.effect.IO
import io.circe.literal.JsonStringContext
import io.circe.syntax.EncoderOps
import weaver.SimpleIOSuite

object CodecTest extends SimpleIOSuite {

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

    for {
      modelRepresentation <- IO.fromEither(json.as[Pokemon])
      jsonRepresentation  <- IO(modelRepresentation.asJson)
    } yield expect(jsonRepresentation == json)

  }

}
