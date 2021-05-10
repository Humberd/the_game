package core.maps.entities.creatures.monster

import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Fixture
import core.maps.entities.CollisionCategory
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.player.Player
import ktx.box2d.circle
import mu.KLogging

class MonsterAiMovement(private val monster: Monster) {
    companion object : KLogging()

    lateinit var sensorArea: Fixture
    var currentlyFollowing: Player? = null

    fun onInit() {
        sensorArea = monster.physics.body.circle(monster.detectionRadius) {
            density = 0f
            friction = 0f
            restitution = 0f
            userData = this@MonsterAiMovement
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

        updateSensorRadius(monster.chaseRadius)
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

        updateSensorRadius(monster.detectionRadius)
        currentlyFollowing = null
        logger.info { "Stop following $creature" }
    }

    fun afterPhysicsUpdate(deltaTime: Float) {
        if (monster.movement.isMoving() && currentlyFollowing == null) {
            monster.movement.stopMoving()
        } else if (currentlyFollowing != null) {
            monster.movement.startMovingTo(currentlyFollowing?.position!!)
        }
    }

    private fun updateSensorRadius(radius: Float) {
        val shape = sensorArea.shape
        require(shape is CircleShape)
        shape.radius = radius
    }
}
