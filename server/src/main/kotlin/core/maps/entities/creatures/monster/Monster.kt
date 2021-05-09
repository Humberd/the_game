package core.maps.entities.creatures.monster

import core.StateChangeNotifier
import core.maps.entities.CollisionCategory
import core.maps.entities.GameMap
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.CreatureSeed
import core.maps.entities.creatures.player.Player
import ktx.box2d.circle
import mu.KLogging

class Monster(
    creatureSeed: CreatureSeed,
    gameMap: GameMap,
    notifier: StateChangeNotifier,
    monsterSeed: MonsterSeed
) : Creature(creatureSeed, gameMap, notifier) {
    companion object : KLogging()

    val detection = DetectionArea(monsterSeed.detectionRadius)
    val chaseRadius = monsterSeed.chaseRadius

    inner class DetectionArea(val radius: Float) {
        var currentlyFollowing: Player? = null

        fun onInit() {
            physics.body.circle(radius) {
                density = 0f
                friction = 0f
                restitution = 0f
                userData = this@DetectionArea
                isSensor = true
                filter.categoryBits = CollisionCategory.DETECTION.value
                filter.maskBits = CollisionCategory.DETECTION.collidesWith()
            }
        }

        fun onCollisionStartWith(creature: Creature) {
            if (creature !is Player) {
                return
            }

            if (currentlyFollowing != null) {
                return
            }

            currentlyFollowing = creature
            logger.info { "Start following $creature" }
        }

        fun onCollisionEndWith(creature: Creature) {
            if (creature !is Player) {
                return
            }

            if (currentlyFollowing == null) {
                return
            }
            currentlyFollowing = null
            movement.stopMoving()
            logger.info { "Stop following $creature" }
        }
    }

    override val hooks = MonsterHooks()
    override val collisionCategory = CollisionCategory.MONSTER

    override fun onInit() {
        super.onInit()
        detection.onInit()

//        physics.body.circle(radius = chaseRadius) {
//            density = 0f
//            friction = 0f
//            restitution = 0f
//            userData = this@Monster
//            isSensor = true
//            filter.categoryBits = CollisionCategory.DETECTION.value
//            filter.maskBits = CollisionCategory.DETECTION.collidesWith()
//        }
    }

    override fun afterPhysicsUpdate(deltaTime: Float) {
        super.afterPhysicsUpdate(deltaTime)

        detection.currentlyFollowing?.let {
            movement.startMovingTo(it.position)
        }
    }

    override fun toString() = "Monster($cid)"
}
