package core

import core.types.PID
import core.types.WorldPosition
import org.mini2Dx.gdx.math.Vector2

inline class PlayerName(val value: String)

class PlayerCharacter(
    val id: PID,
    val name: PlayerName,
    val health: UInt,
    var position: WorldPosition = Vector2(400f, 400f)
) {
    var movementSpeed = 4f
    val viewRadius: UByte = 5u
}
