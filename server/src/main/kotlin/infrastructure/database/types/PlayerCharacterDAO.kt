package infrastructure.database.types

import core.types.CreatureName
import core.types.PID
import core.types.SpriteId

data class PlayerCharacterDAO(
    val id: PID,
    val name: CreatureName,
    val health: UInt,
    val spriteId: SpriteId
)
