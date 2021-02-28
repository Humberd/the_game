package infrastructure.database

import core.PlayerName
import core.types.PID
import infrastructure.database.types.PlayerCharacterDAO

class Database {
    private val chars: HashMap<PID, PlayerCharacterDAO>

    init {
         chars = hashMapOf(
             PID(1u) to PlayerCharacterDAO(
                 id = PID(1u),
                 name = PlayerName("Alice"),
                 health = 100u
             ),
             PID(2u) to PlayerCharacterDAO(
                 id = PID(2u),
                 name = PlayerName("Bob1"),
                 health = 80u
             )
         )
    }

    fun getPlayer(pid: PID): PlayerCharacterDAO {
        return chars[pid] ?: throw Error("PlayerCharacterDao not found for ${pid}")
    }
}
