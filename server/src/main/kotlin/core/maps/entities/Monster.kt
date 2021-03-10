package core.maps.entities

import core.AsyncGameTask
import core.GameLoop
import core.types.*
import errors.NOT_REACHED
import org.mini2Dx.gdx.math.Vector2
import utils.ms
import utils.sec

class Monster(
    cid: CID,
    name: CreatureName,
    health: UInt,
    spriteId: SpriteId,
    position: WorldPosition = Vector2(0f, 0f)
) : Creature(cid, name, health, spriteId, position) {

    val attackTriggerRadius = WorldRadius(85)
    private var isWalking = false


    fun startWalking() {
        if (isWalking) {
            return
        }
        isWalking = true

        walk(Vector2(0f, 1f))
    }

    /**
     * 0,1
     * 1,0
     * 0,-1
     * -1,0
     */
    private fun walk(direction: Vector2) {
        registerAsyncTask(onFinish = {
            if (direction.y == 1f) {
                walk(Vector2(1f, 0f))
            } else if (direction.y == -1f) {
                walk(Vector2(-1f, 0f))
            } else if (direction.x == 1f) {
                walk(Vector2(0f, -1f))
            } else if (direction.x == -1f) {
                walk(Vector2(0f, 1f))
            } else {
                NOT_REACHED()
            }
        }) {
            scriptable.moveBy(direction)
        }
    }

    private fun registerAsyncTask(onFinish: () -> Unit, callback: () -> Unit) {
        GameLoop.instance.requestAsyncTask(AsyncGameTask(2.ms, 1.sec, onFinish, callback))
    }
}
