package infrastructure.database.types

import core.PlayerName
import core.types.PID

data class PlayerCharacterDAO(
    val id: PID,
    val name: PlayerName,
    val health: UInt
)
