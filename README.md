## Pokemon Database project


This project will be revolving around creating a simple Http service that will store information about Pokemons. 


We want to have the following routes 
```
GET /v1/pokemon - returning List of all Pokemons in the resistance layer
GET /v1/pokemon/{type} - Returning list of all Pokemons of given type ( empty list if none)
GET /v1/pokemon/{id} - Return Option of Pokemon by id (None if ID missing in db )

POST /v1/pokemon - Adds new pokemon to the database 
PATCH /v1/pokemon/{id} - Update Pokemon info by id (404 if wrong id)
DELETE /v1/pokemon/{id} - deletes Pokemon by id ( 404 if wrong id )
```

For the simplicity of this task the resistance layer will be in memory , so no external DB involved ( YET! )
There are objects called Task here and there , they just have task description. Feel free to read them and delete them
after the task is completed. I wrote them in object rather than md file just out of my preference. 

Hints:

1. The capability you look for is Applicative. If F is an applicative it means it can wrap around other things
so to tell Scala that your F can do that you need to define a type bound on the make function
   def make[F[_] : Applicative]: PersistenceLayer[F] 
This says that make can be called returning any effect that has Applicative capabilities 
and then you can wrap the return type from Map using Applicative[F].pure(???).

2. The changes in sbt wont be reflected in the project until you will read the build file. Turn on sbt in your terminal
 and use reload command. 

3. To run tests use test sbt command. you Can also use testOnly for running just specific test suite. For now there is just
 one so the test command should be enough. 