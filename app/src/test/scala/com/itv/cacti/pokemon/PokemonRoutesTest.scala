import cats.effect.IO
import io.circe.syntax.EncoderOps
import java.util.UUID
import org.http4s.HttpApp
import org.http4s.Method.DELETE
import org.http4s.Method.GET
import org.http4s.Method.PATCH
import org.http4s.Method.POST
import org.http4s.Request
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.implicits.http4sLiteralsSyntax
import scala.collection.mutable
import weaver.SimpleIOSuite

import com.itv.cacti.core.Ability
import com.itv.cacti.core.OperationStatus
import com.itv.cacti.core.Pokemon
import com.itv.cacti.core.PokemonDescription
import com.itv.cacti.core.PokemonLevel
import com.itv.cacti.core.PokemonName
import com.itv.cacti.core.PokemonType
import com.itv.cacti.db.PersistenceLayer
import com.itv.cacti.pokemon.routes.PokemonRoutes

object PokemonRoutesTest extends SimpleIOSuite {

  def dbMock(pokemonMap: mutable.Map[UUID, Pokemon]) =
    new PersistenceLayer[IO] {

      override def getById(id: UUID): IO[Either[PokemonNotFound, Pokemon]] =
        IO.pure(
          pokemonMap.get(id).toRight(PokemonNotFound("Pokemon not found"))
        )

      override def getByType(`type`: PokemonType): IO[List[Pokemon]] =
        IO.pure(
          pokemonMap.values.filter(_.`type`.contains(`type`)).toList
        )

      override def getAll: IO[List[(UUID, Pokemon)]] =
        IO.pure(
          pokemonMap.toList
        )

      override def add(pokemon: Pokemon, id: UUID): IO[Unit] =
        IO.pure(
          pokemonMap.addOne((id, pokemon))
        )

      override def replace(
          pokemon: Pokemon,
          uuid: UUID
      ): IO[Either[PokemonNotFound, OperationStatus]] = {

        IO.pure {
          pokemonMap
            .get(uuid)
            .map(_ => pokemonMap.update(uuid, pokemon))
            .map(_ => OperationStatus.Replaced)
            .toRight(PokemonNotFound("Pokemon not found , could not replace"))

        }

      }

      override def delete(
          uuid: UUID
      ): IO[Either[PokemonNotFound, OperationStatus]] =
        IO.pure {
          pokemonMap
            .remove(uuid)
            .map(_ => OperationStatus.Deleted)
            .toRight(PokemonNotFound("Could not delete, not found"))

        }

    }

  test("POST Pokemon route") {

    val pokemon = Pokemon.apply(
      name = PokemonName("Mew"),
      description = PokemonDescription("Super rare , super powerful"),
      `type` = List(PokemonType.Psychic),
      level = PokemonLevel(999),
      abilities = List.empty[Ability]
    )

    val persistenceLayer = dbMock(pokemonMap = mutable.Map.empty[UUID, Pokemon])

    val httpApp: HttpApp[IO] =
      PokemonRoutes.make[IO](persistenceLayer).routes.orNotFound

    for {
      request <- IO.pure(
        Request[IO](method = POST, uri = uri"/v1/pokemon")
          .withEntity(pokemon.asJson)
      )
      response <- httpApp.run(request)
    } yield expect(response.status.code == 201)

  }

  test("GET request returns a list of pokemon") {
    val uuid = UUID.randomUUID()
    val pokemon = Pokemon.apply(
      name = PokemonName("Mewtwo"),
      description = PokemonDescription("Super rare , super powerful"),
      `type` = List(PokemonType.Psychic),
      level = PokemonLevel(999),
      abilities = List.empty[Ability]
    )

    val persistenceLayer = dbMock(mutable.Map[UUID, Pokemon] {
      uuid -> pokemon
    })

    val httpApp: HttpApp[IO] =
      PokemonRoutes.make[IO](persistenceLayer).routes.orNotFound

    for {
      request <- IO.pure(
        Request[IO](method = GET, uri = uri"/v1/pokemon")
      )

      response <- httpApp.run(request)
      body     <- response.as[List[(UUID, Pokemon)]]
    } yield expect(
      response.status.code == 200 && body == List[(UUID, Pokemon)](
        (uuid, pokemon)
      )
    )
  }

  test("GET request returns a pokemon by id") {
    val uuid = UUID.randomUUID()
    val pokemon = Pokemon.apply(
      name = PokemonName("Mewtwo"),
      description = PokemonDescription("Super rare , super powerful"),
      `type` = List(PokemonType.Psychic),
      level = PokemonLevel(999),
      abilities = List.empty[Ability]
    )

    val persistenceLayer = dbMock(mutable.Map[UUID, Pokemon] {
      uuid -> pokemon
    })

    val httpApp: HttpApp[IO] =
      PokemonRoutes.make[IO](persistenceLayer).routes.orNotFound

    for {
      request <- IO.pure(
        Request[IO](method = GET, uri = uri"/v1/pokemon" / uuid)
      )

      response <- httpApp.run(request)
      body     <- response.as[Pokemon]
    } yield expect(response.status.code == 200 && body == pokemon)
  }

  test("GET request returns a pokemon by pokemonType") {
    val uuid = UUID.randomUUID()
    val pokemon = Pokemon.apply(
      name = PokemonName("Mewtwo"),
      description = PokemonDescription("Super rare , super powerful"),
      `type` = List(PokemonType.Psychic),
      level = PokemonLevel(999),
      abilities = List.empty[Ability]
    )

    val persistenceLayer = dbMock(mutable.Map[UUID, Pokemon] {
      uuid -> pokemon
    })

    val httpApp: HttpApp[IO] =
      PokemonRoutes.make[IO](persistenceLayer).routes.orNotFound

    for {
      request <- IO.pure(
        Request[IO](
          method = GET,
          uri = uri"/v1/pokemon" / PokemonType.Psychic.toString
        )
      )

      response <- httpApp.run(request)
      body     <- response.as[List[Pokemon]]
    } yield expect(response.status.code == 200 && body == List(pokemon))
  }

  test("PATCH request to update a pokemon") {
    val uuid = UUID.randomUUID()
    val pokemon = Pokemon.apply(
      name = PokemonName("Mewtwo"),
      description = PokemonDescription("Super rare , super powerful"),
      `type` = List(PokemonType.Psychic),
      level = PokemonLevel(999),
      abilities = List.empty[Ability]
    )

    val updatedPokemon = pokemon.copy(name = PokemonName("hi"))

    val persistenceLayer = dbMock(mutable.Map[UUID, Pokemon] {
      uuid -> pokemon
    })

    val httpApp: HttpApp[IO] =
      PokemonRoutes.make[IO](persistenceLayer).routes.orNotFound

    for {
      request <- IO.pure(
        Request[IO](
          method = PATCH,
          uri = uri"/v1/pokemon" / uuid
        ).withEntity(updatedPokemon)
      )

      response <- httpApp.run(request)

      getRequest <- IO.pure(
        Request[IO](
          method = GET,
          uri = uri"/v1/pokemon" / uuid
        )
      )
      finalResponse <- httpApp.run(getRequest)
      body          <- finalResponse.as[Pokemon]
    } yield expect(response.status.code == 200 && body == updatedPokemon)
  }

  test("DELETE request to remove a pokemon of the list") {
    val uuid = UUID.randomUUID()
    val pokemon = Pokemon.apply(
      name = PokemonName("Mewtwo"),
      description = PokemonDescription("Super rare , super powerful"),
      `type` = List(PokemonType.Psychic),
      level = PokemonLevel(999),
      abilities = List.empty[Ability]
    )

    val persistenceLayer = dbMock(mutable.Map[UUID, Pokemon] {
      uuid -> pokemon
    })

    val httpApp: HttpApp[IO] =
      PokemonRoutes.make[IO](persistenceLayer).routes.orNotFound

    for {
      request <- IO.pure(
        Request[IO](
          method = DELETE,
          uri = uri"/v1/pokemon" / uuid
        )
      )

      response <- httpApp.run(request)

      getRequest <- IO.pure(
        Request[IO](
          method = GET,
          uri = uri"/v1/pokemon"
        )
      )
      finalResponse <- httpApp.run(getRequest)
      body          <- finalResponse.as[List[(UUID, Pokemon)]]
    } yield expect(
      response.status.code == 200 && body == List.empty[(UUID, Pokemon)]
    )

  }

}
