package infrastructure.database

import core.types.CreatureName
import core.types.PID
import core.types.SpriteId
import infrastructure.database.types.PlayerCharacterDAO

class Database {
    private val chars: HashMap<PID, PlayerCharacterDAO>

    init {
         chars = hashMapOf(
             PID(1u) to PlayerCharacterDAO(
                 id = PID(1u),
                 name = CreatureName("Alice"),
                 health = 100u,
                 spriteId = SpriteId(5u)
             ),
             PID(2u) to PlayerCharacterDAO(
                 id = PID(2u),
                 name = CreatureName("Bob1"),
                 health = 80u,
                 spriteId = SpriteId(5u)
             )
         )
    }

    fun getPlayer(pid: PID): PlayerCharacterDAO {
        return chars[pid] ?: throw Error("PlayerCharacterDao not found for ${pid}")
    }
}
