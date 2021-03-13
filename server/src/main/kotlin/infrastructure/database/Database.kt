package infrastructure.database

import core.types.*
import infrastructure.database.types.PlayerCharacterDAO

class Database {
    private val chars: HashMap<PID, PlayerCharacterDAO>

    init {
         chars = hashMapOf(
             PID(1u) to PlayerCharacterDAO(
                 pid = PID(1u),
                 name = CreatureName("Alice"),
                 baseHealth = 150u,
                 currentHealth = 100u,
                 spriteId = SpriteId(5u),
                 position = WorldPosition(3f, 3f),
                 velocity = 3f,
                 tilesViewRadius = TileRadius(3),
                 bodyRadius = 0.5f
             ),
             PID(2u) to PlayerCharacterDAO(
                 pid = PID(2u),
                 name = CreatureName("Bob"),
                 baseHealth = 150u,
                 currentHealth = 30u,
                 spriteId = SpriteId(5u),
                 position = WorldPosition(5f, 4.5f),
                 velocity = 2.5f,
                 tilesViewRadius = TileRadius(1),
                 bodyRadius = 0.75f
             )
         )
    }

    fun getPlayer(pid: PID): PlayerCharacterDAO {
        return chars[pid] ?: throw Error("PlayerCharacterDao not found for ${pid}")
    }
}
