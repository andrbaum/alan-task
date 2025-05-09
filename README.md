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


1. You need to find right set of capabilities for routes to work. The compiler should tell you which one it needs for the code
to compile. Try to figure this out in that way , if stuck for more than an hours , let me know! 
2. The Http4sDSL brings the syntax that lets you define routes. Given that it will also work within some
abstract F , the bounds need to exist for this to work. There are many different way to bring th eDSL to scope like brinigng it
via import to the whole scope. Imp the inheritance is clearer 
3. Unapply method is what you are after! Apply lets you create something , unapply lets you try to create something.
It is used by extracting parameters from URI 