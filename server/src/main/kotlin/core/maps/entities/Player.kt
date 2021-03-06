package core.maps.entities

import core.types.*
import org.mini2Dx.gdx.math.Vector2

class Player(
    val pid: PID,
    cid: CID,
    name: CreatureName,
    health: UInt,
    spriteId: SpriteId,
    position: WorldPosition = Vector2(0f, 0f)
) : Creature(cid, name, health, spriteId, position) {
    override fun toString(): String {
        return pid.toString()
    }
}
