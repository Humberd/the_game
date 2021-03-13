package core.maps.entities

import com.badlogic.gdx.math.Vector2
import core.AsyncGameTask
import core.GameLoop
import core.types.CID
import core.types.CreatureName
import core.types.SpriteId
import core.types.WorldRadius
import errors.NOT_REACHED
import utils.ms
import utils.sec

class Monster(
    cid: CID,
    name: CreatureName,
    health: UInt,
    spriteId: SpriteId,
) : Creature(cid, name, health, spriteId) {

    val attackTriggerRadius = WorldRadius(85)
    private var isWalking = false

    private var isAttacking = false
    private var asyncTask: AsyncGameTask? = null

    override fun onOtherCreatureDisappearFromViewRange(otherCreature: Creature) {
        if (otherCreature is Player) {
            if (isAttacking) {
                stopAttacking()
            }
        }
    }

    override fun onOtherCreatureAppearInViewRange(otherCreature: Creature) {
//        if (otherCreature is Player) {
//            if (!isAttacking) {
//                tryStartToAttack(otherCreature)
//            } else {
//                tryStopToAttack(otherCreature)
//            }
//        }
    }

    override fun onOtherCreaturePositionChange(otherCreature: Creature) {
//        if (otherCreature is Player) {
//            if (!isAttacking) {
//                tryStartToAttack(otherCreature)
//            } else {
//                tryStopToAttack(otherCreature)
//            }
//        }
    }

    private fun stopAttacking() {
        isAttacking = false
        asyncTask?.cancel()
        asyncTask = null
    }

    private fun startAttacking(otherCreature: Player) {
        isAttacking = true
        asyncTask = GameLoop.instance.requestAsyncTask(AsyncGameTask(1.sec, 40.sec, { stopAttacking() }) {
            otherCreature.takeDamage(2u)
            if (otherCreature.isDead()) {
                stopAttacking()
            }
        })
    }

    private fun tryStartToAttack(otherCreature: Player) {
//        val totalDistance = getDistance(this.position, otherCreature.position)
//        val maxDistToAttack = attackTriggerRadius.value + otherCreature.bodyRadius.value
//        if (totalDistance < maxDistToAttack) {
//            startAttacking(otherCreature)
//        }
    }


    private fun tryStopToAttack(otherCreature: Player) {
//        val totalDistance = getDistance(this.position, otherCreature.position)
//        val maxDistToAttack = attackTriggerRadius.value + otherCreature.bodyRadius.value
//        if (totalDistance >= maxDistToAttack) {
//            stopAttacking()
//        }
    }


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

    private fun registerAsyncTask(onFinish: () -> Unit, callback: () -> Unit): AsyncGameTask {
        return GameLoop.instance.requestAsyncTask(AsyncGameTask(2.ms, 1.sec, onFinish, callback))
    }
}
