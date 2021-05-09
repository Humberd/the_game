package infrastructure.database

import core.types.*
import infrastructure.database.types.ItemDAO
import infrastructure.database.types.PlayerCharacterDAO
import pl.humberd.models.Experience
import pl.humberd.models.PID

class Database {
    //region Characters
    private val chars: HashMap<PID, PlayerCharacterDAO>

    init {
         chars = hashMapOf(
             PID(1u) to PlayerCharacterDAO(
                 pid = PID(1u),
                 name = CreatureName("Alice"),
                 experience = Experience(3232L),
                 spriteId = SpriteId(5u),
                 position = WorldPosition(3f, 3f),
                 tilesViewRadius = TileRadius(3),
                 bodyRadius = 0.5f
             ),
             PID(2u) to PlayerCharacterDAO(
                 pid = PID(2u),
                 name = CreatureName("Bob"),
                 experience = Experience(3511L),
                 spriteId = SpriteId(5u),
                 position = WorldPosition(5f, 4.5f),
                 tilesViewRadius = TileRadius(2),
                 bodyRadius = 0.5f
             )
         )
    }

    fun getPlayer(pid: PID): PlayerCharacterDAO {
        return chars[pid] ?: throw Error("PlayerCharacterDao not found for ${pid}")
    }
    //endregion

    //region Items
    private val items: HashMap<IID, ItemDAO>
    init {
        items = hashMapOf(
//            IID(1u) to ItemDAO(
//                iid = IID(1u),
//                name = "Brana Shield",
//                resourceId = ResourceId(1u),
//                equippable = Equippable.within(Equippable.Slot.LEFT_HAND, Equippable.Slot.RIGHT_HAND),
//                modifiers = listOf(
//                    ModifierDAO(
//                        attribute = "defence",
//                        value = 25,
//                        type = "flat"
//                    ),
//                    ModifierDAO(
//                        attribute = "attack",
//                        value = -5,
//                        type = "flat"
//                    ),
//                    ModifierDAO(
//                        attribute = "attack_speed",
//                        value = 10,
//                        type = "percent"
//                    )
//                )
//            )
        )
    }

    //endregion
}
