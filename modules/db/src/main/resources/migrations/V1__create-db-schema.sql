
-- Enable the uuid-ossp extension (only needs to be done once per DB)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enum type for Pokemon types
CREATE TYPE pokemon_type AS ENUM (
    'Fire', 'Water', 'Grass', 'Electric', 'Psychic', 'Ice', 'Dragon', 'Dark', 'Fairy',
    'Normal', 'Fighting', 'Flying', 'Poison', 'Ground', 'Rock', 'Bug', 'Ghost', 'Steel'
);

-- Pokemon table
CREATE TABLE pokemon (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    description TEXT,
    level INT NOT NULL
);

-- Ability table
CREATE TABLE ability (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    damage INT NOT NULL
);

-- Join table: pokemon <-> ability (many-to-many)
CREATE TABLE pokemon_ability (
    pokemon_id UUID REFERENCES pokemon(id) ON DELETE CASCADE,
    ability_id UUID REFERENCES ability(id) ON DELETE CASCADE,
    PRIMARY KEY (pokemon_id, ability_id)
);

-- Join table: pokemon <-> pokemon_type (many-to-many)
CREATE TABLE pokemon_type_link (
    pokemon_id UUID REFERENCES pokemon(id) ON DELETE CASCADE,
    type pokemon_type NOT NULL,
    PRIMARY KEY (pokemon_id, type)
);

-- Join table: ability <-> pokemon_type (many-to-many)
CREATE TABLE ability_type_link (
    ability_id UUID REFERENCES ability(id) ON DELETE CASCADE,
    type pokemon_type NOT NULL,
    PRIMARY KEY (ability_id, type)
);

INSERT INTO ability (id, name, damage) VALUES
  (uuid_generate_v4(), 'Flamethrower', 90),
  (uuid_generate_v4(), 'Hydro Pump', 110),
  (uuid_generate_v4(), 'Thunderbolt', 90),
  (uuid_generate_v4(), 'Solar Beam', 120),
  (uuid_generate_v4(), 'Ice Beam', 95),
  (uuid_generate_v4(), 'Shadow Ball', 80),
  (uuid_generate_v4(), 'Sing', 0),
  (uuid_generate_v4(), 'Karate Chop', 50),
  (uuid_generate_v4(), 'Rock Throw', 50),
  (uuid_generate_v4(), 'Tackle', 40);

INSERT INTO pokemon (id, name, description, level) VALUES
  (uuid_generate_v4(), 'Charmander', 'A fire-type lizard Pokémon.', 12),
  (uuid_generate_v4(), 'Squirtle', 'A water-type turtle Pokémon.', 10),
  (uuid_generate_v4(), 'Bulbasaur', 'A grass/poison-type Pokémon.', 11),
  (uuid_generate_v4(), 'Pikachu', 'An electric-type mouse Pokémon.', 15),
  (uuid_generate_v4(), 'Jigglypuff', 'A fairy-type singing Pokémon.', 8),
  (uuid_generate_v4(), 'Gengar', 'A ghost/poison-type Pokémon.', 25),
  (uuid_generate_v4(), 'Machop', 'A fighting-type Pokémon.', 18),
  (uuid_generate_v4(), 'Onix', 'A rock/ground-type Pokémon.', 20),
  (uuid_generate_v4(), 'Dratini', 'A dragon-type Pokémon.', 22),
  (uuid_generate_v4(), 'Eevee', 'A normal-type Pokémon with many evolutions.', 14);


INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Fire' FROM pokemon WHERE name = 'Charmander';

INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Water' FROM pokemon WHERE name = 'Squirtle';

INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Grass' FROM pokemon WHERE name = 'Bulbasaur';
INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Poison' FROM pokemon WHERE name = 'Bulbasaur';

INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Electric' FROM pokemon WHERE name = 'Pikachu';

INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Fairy' FROM pokemon WHERE name = 'Jigglypuff';

INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Ghost' FROM pokemon WHERE name = 'Gengar';
INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Poison' FROM pokemon WHERE name = 'Gengar';

INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Fighting' FROM pokemon WHERE name = 'Machop';

INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Rock' FROM pokemon WHERE name = 'Onix';
INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Ground' FROM pokemon WHERE name = 'Onix';

INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Dragon' FROM pokemon WHERE name = 'Dratini';

INSERT INTO pokemon_type_link (pokemon_id, type)
SELECT id, 'Normal' FROM pokemon WHERE name = 'Eevee';

-- Link Flamethrower to Fire type
INSERT INTO ability_type_link (ability_id, type)
SELECT id, 'Fire' FROM ability WHERE name = 'Flamethrower';

-- Hydro Pump -> Water
INSERT INTO ability_type_link (ability_id, type)
SELECT id, 'Water' FROM ability WHERE name = 'Hydro Pump';

-- Thunderbolt -> Electric
INSERT INTO ability_type_link (ability_id, type)
SELECT id, 'Electric' FROM ability WHERE name = 'Thunderbolt';

-- Solar Beam -> Grass
INSERT INTO ability_type_link (ability_id, type)
SELECT id, 'Grass' FROM ability WHERE name = 'Solar Beam';

-- Ice Beam -> Ice
INSERT INTO ability_type_link (ability_id, type)
SELECT id, 'Ice' FROM ability WHERE name = 'Ice Beam';

-- Shadow Ball -> Ghost
INSERT INTO ability_type_link (ability_id, type)
SELECT id, 'Ghost' FROM ability WHERE name = 'Shadow Ball';

-- Sing -> Fairy
INSERT INTO ability_type_link (ability_id, type)
SELECT id, 'Fairy' FROM ability WHERE name = 'Sing';

-- Karate Chop -> Fighting
INSERT INTO ability_type_link (ability_id, type)
SELECT id, 'Fighting' FROM ability WHERE name = 'Karate Chop';

-- Rock Throw -> Rock
INSERT INTO ability_type_link (ability_id, type)
SELECT id, 'Rock' FROM ability WHERE name = 'Rock Throw';

-- Tackle -> Normal
INSERT INTO ability_type_link (ability_id, type)
SELECT id, 'Normal' FROM ability WHERE name = 'Tackle';


-- Charmander -> Flamethrower
INSERT INTO pokemon_ability (pokemon_id, ability_id)
SELECT p.id, a.id FROM pokemon p, ability a WHERE p.name = 'Charmander' AND a.name = 'Flamethrower';

-- Squirtle -> Hydro Pump
INSERT INTO pokemon_ability (pokemon_id, ability_id)
SELECT p.id, a.id FROM pokemon p, ability a WHERE p.name = 'Squirtle' AND a.name = 'Hydro Pump';

-- Pikachu -> Thunderbolt
INSERT INTO pokemon_ability (pokemon_id, ability_id)
SELECT p.id, a.id FROM pokemon p, ability a WHERE p.name = 'Pikachu' AND a.name = 'Thunderbolt';

-- Bulbasaur -> Solar Beam
INSERT INTO pokemon_ability (pokemon_id, ability_id)
SELECT p.id, a.id FROM pokemon p, ability a WHERE p.name = 'Bulbasaur' AND a.name = 'Solar Beam';

-- Dratini -> Ice Beam
INSERT INTO pokemon_ability (pokemon_id, ability_id)
SELECT p.id, a.id FROM pokemon p, ability a WHERE p.name = 'Dratini' AND a.name = 'Ice Beam';

-- Gengar -> Shadow Ball
INSERT INTO pokemon_ability (pokemon_id, ability_id)
SELECT p.id, a.id FROM pokemon p, ability a WHERE p.name = 'Gengar' AND a.name = 'Shadow Ball';

-- Jigglypuff -> Sing
INSERT INTO pokemon_ability (pokemon_id, ability_id)
SELECT p.id, a.id FROM pokemon p, ability a WHERE p.name = 'Jigglypuff' AND a.name = 'Sing';

-- Machop -> Karate Chop
INSERT INTO pokemon_ability (pokemon_id, ability_id)
SELECT p.id, a.id FROM pokemon p, ability a WHERE p.name = 'Machop' AND a.name = 'Karate Chop';

-- Onix -> Rock Throw
INSERT INTO pokemon_ability (pokemon_id, ability_id)
SELECT p.id, a.id FROM pokemon p, ability a WHERE p.name = 'Onix' AND a.name = 'Rock Throw';

-- Eevee -> Tackle
INSERT INTO pokemon_ability (pokemon_id, ability_id)
SELECT p.id, a.id FROM pokemon p, ability a WHERE p.name = 'Eevee' AND a.name = 'Tackle';
