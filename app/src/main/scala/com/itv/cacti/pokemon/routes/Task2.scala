package com.itv.cacti.pokemon.routes

object Task2 {

  /**
   * Task 2
   *
   * So now we have out project set up and the persistence layer existing we can use it for some good
   *
   * Your goals is to define a new class that will represents the routes server by our api
   * The set up is the same as for the most situations
   *
   * - you want to define final class that will operate in context of some abstract F Hint.1
   * - define the class dependencies ( persistence layer in this case )
   * - Bring Http4sDsl for the given F to the scope Hint.2
   * - define companion object with make function that returns an instance of PokemonRoutes
   *
   * Side note : the companion object might seem redundant , and it kind of is at this point but we will use this set up later
   *
   * Pay particular focus on impliocits! You will need a lot of them for the whole thing to work
   *
   * Good thing imo is to start with always having
   *
   * import cats.implicits._
   *
   * present. That's where the power of cats comes from. I will let you figure out other ones as part of the exercise
   *
   * REMEMBER ABOUT THE CONTEXT BOUNDS OF THE F !!
   *
   * Routes:
   *
   * GET /v1/pokemon - returning List of all Pokemons in the resistance layer
   * GET /v1/pokemon/{type} - Returning list of all Pokemons of given type ( empty list if none)
   * GET /v1/pokemon/{id} - Return Option of Pokemon by id (None if ID missing in db )
   *
   * POST /v1/pokemon - Adds new pokemon to the database
   * PATCH /v1/pokemon/{id} - Update Pokemon info by id (404 if wrong id)
   * DELETE /v1/pokemon/{id} - deletes Pokemon by id ( 404 if wrong id )
   *
   * You also would like to be able to decode the values from the URI path itself
   * this way you will be able to get for example PokemonType out of the URI , rather than hawing a string
   * and trying to get PokeMonType out of it
   *
   * Hint 3
   *
   * Http4s documentation is your best friend here , they have really good examples.
   *
   * https://http4s.org/v0.23/docs/quickstart.html
   *
   * */

}
