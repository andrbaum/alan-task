import org.http4s.circe.CirceEntityDecoder._

import cats.effect.IO
import io.circe.syntax.EncoderOps
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.Method.POST
import org.http4s.Method.GET
import org.http4s.Method.PATCH
import org.http4s.Method.DELETE
import scala.collection.mutable
import org.http4s.Request
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.implicits.http4sLiteralsSyntax
import weaver.SimpleIOSuite

import com.itv.cacti.core.Ability
import com.itv.cacti.core.Pokemon
import com.itv.cacti.core.PokemonDescription
import com.itv.cacti.core.PokemonLevel
import com.itv.cacti.core.PokemonName
import com.itv.cacti.core.PokemonType
import com.itv.cacti.db.PersistenceLayer
import com.itv.cacti.pokemon.routes.PokemonRoutes
import com.itv.cacti.db.PokemonUtil.pokemonMap
import java.util.UUID
import com.itv.cacti.core.OperationStatus

object PokemonRoutesTest extends SimpleIOSuite {

  /** First we need to stub our routes so we can run tests against them. Given
    * that the persistence layer atm is in memory and defined by us it is
    * suitable to be used as a stub db, we will have to change that later
    *
    * But what is HttpApp over all ? Diving into the library we will eventually
    * find this
    *
    * type HttpApp[F[_]] = Http[F, F]
    *
    * type Http[F[_], G[_]] = Kleisli[F, Request[G], Response[G]]
    *
    * So HttpApp is just an alias for Kleisli[F, Request[G], Response[G]].
    * Kleisli is just another type introduced by Cats Effect and it is very
    * handy!
    *
    * It encapsulate the transformation from A => F[B] so
    *
    * A => F[B] == Kleisli[F, A, B]
    *
    * ( From Some generic type A to F of another , different generic type
    *
    * In context of Http it reads as
    *
    * Request[G] => F[Response[G]]
    *
    * so it is a transformation that describes going from Request to some effect
    * F that will eventually produce Response for us. Both Request and Response
    * need to operate within the same context G
    *
    * Recall what we spoke about while exploring the difference between
    * IO[PersistenceLayer[IO]] and PersistenceLayer[IO]
    *
    * This gives us ability to describe transformations without actually running
    * them making composition super easy
    */

  val httpApp: HttpApp[IO] =
    PokemonRoutes.make[IO](PersistenceLayer.make[IO]).routes.orNotFound

  val httpRoutes: HttpRoutes[IO] =
    PokemonRoutes.make[IO](PersistenceLayer.make[IO]).routes

  /** There is subtle difference between HttpApp and HttpRoutes
    *
    * type HttpRoutes[F[_]] = Http[OptionT[F, *], F]
    *
    * type HttpApp[F[_]] = Http[F, F]
    *
    * lets expand this type aliases
    *
    * HttpRoutes [F[_]] == Http[OptionT[F,*], F] == Kleisli[OptionT[F,*],
    * Request[F], Response[F]] == Request[F] => OptionT[F,Response[F]]
    *
    * HttpApp [F[_]] == Http[F, F] == Kleisli[F, Request[F], Response[F]] ==
    * Request[F] => F[Response[F]]
    *
    * Try to spot the difference! Dont worry about OptionT[F, A] at the moment.
    * You can reason about it the same way as of F[Option[A]]
    */

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

  test("Example test") {
    IO(expect(true))
  }

  test("POST Pokemon route") {

    // First we need to get something to feed as our body
    val pokemon = Pokemon.apply(
      name = PokemonName("Mew"),
      description = PokemonDescription("Super rare , super powerful"),
      `type` = List(PokemonType.Psychic),
      level = PokemonLevel(999),
      abilities = List.empty[Ability]
    )

    for {

      // We wrap request creation in IO
      // We Contrast the request using the constructor
      // And we enrich the request by adding entity to it using .withEntity method
      // We need EntityEncoder for .asJson to work
      // but we can skip that by brining an import that will handle this for us
      // import org.http4s.circe.CirceEntityCodec.circeEntityEncoder - you cna use it in the routes as well!

      request <- IO.pure(
        Request[IO](method = POST, uri = uri"/v1/pokemon")
          .withEntity(pokemon.asJson)
      )

      // We run the request against our HttpApp
      response <- httpApp.run(request)

      // we yield an assertion
      // and because we operate with the IO context the return type of this whole operation is
      // IO[Expectations] so the code compiles! ( look example test above )
    } yield expect(response.status.code == 201)

  }

  test("GET request returns a list of pokemon") {
    for {
      request <- IO.pure(
        Request[IO](method = GET, uri = uri"/v1/pokemon")
          .withEntity(pokemonMap.asJson)
      )

      response <- httpApp.run(request)
    } yield expect(response.status.code == 200)
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

    val persistanceStub = dbMock(mutable.Map[UUID, Pokemon] {
      uuid -> pokemon
    })

    val httpApp: HttpApp[IO] =
      PokemonRoutes.make[IO](persistanceStub).routes.orNotFound

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

    val persistanceStub = dbMock(mutable.Map[UUID, Pokemon] {
      uuid -> pokemon
    })

    val httpApp: HttpApp[IO] =
      PokemonRoutes.make[IO](persistanceStub).routes.orNotFound

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

    val persistanceStub = dbMock(mutable.Map[UUID, Pokemon] {
      uuid -> pokemon
    })

    val httpApp: HttpApp[IO] =
      PokemonRoutes.make[IO](persistanceStub).routes.orNotFound

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

    val persistanceStub = dbMock(mutable.Map[UUID, Pokemon] {
      uuid -> pokemon
    })

    val httpApp: HttpApp[IO] =
      PokemonRoutes.make[IO](persistanceStub).routes.orNotFound

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
