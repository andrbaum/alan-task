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


