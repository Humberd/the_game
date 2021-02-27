package core

import core.types.PID
import org.mini2Dx.gdx.math.Vector2

class PlayerCharacter(
    val id: PID
) {
    var movementSpeed = 2f
    var position = Vector2()
}
