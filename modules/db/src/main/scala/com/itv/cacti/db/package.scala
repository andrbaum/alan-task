import com.itv.cacti.core.PokemonName
import com.itv.cacti.core.PokemonDescription
import com.itv.cacti.core.PokemonLevel
import com.itv.cacti.core.PokemonType

package object db {

  import doobie.util.meta.Meta
  import doobie.postgres.implicits._

  implicit val pokemonCoreInfoMeta: Meta[PokemonName] =
    Meta[String].imap[PokemonName](string => PokemonName(string))(pokemonName =>
      pokemonName.name
    )

  implicit val pokemonDescriptionMeta: Meta[PokemonDescription] =
    Meta[String].imap[PokemonDescription](string => PokemonDescription(string))(
      pokemonDescription => pokemonDescription.description
    )

  implicit val pokemonLevel: Meta[PokemonLevel] =
    Meta[Int].imap[PokemonLevel](int => PokemonLevel(int))(pokemonLevel =>
      pokemonLevel.level
    )

  implicit val pokemonType: Meta[PokemonType] = pgEnumStringOpt(
    "pokemon_type",
    PokemonType.withNameInsensitiveOption,
    _.entryName.toLowerCase
  )

}
