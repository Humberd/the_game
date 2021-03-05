package infrastructure.database.types

import core.types.PID
import core.types.PlayerName

data class PlayerCharacterDAO(
    val id: PID,
    val name: PlayerName,
    val health: UInt
)
