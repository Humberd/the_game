package core.maps.entities.creatures.monster

import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Fixture
import core.maps.entities.Collider
import core.maps.entities.CollisionCategory
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.player.Player
import ktx.box2d.circle
import mu.KLogging

class MonsterAiMovement(private val monster: Monster) : Collider.WithCreature {
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

    override fun onCollisionStart(entity: Creature) {
        if (entity !is Player) {
            return
        }

        if (currentlyFollowing != null) {
            return
        }

        updateSensorRadius(monster.chaseRadius)
        currentlyFollowing = entity
        logger.info { "Start following $entity" }
    }

    override fun onCollisionEnd(entity: Creature) {
        if (entity !is Player) {
            return
        }

        if (currentlyFollowing == null) {
            return
        }

        updateSensorRadius(monster.detectionRadius)
        currentlyFollowing = null
        logger.info { "Stop following $entity" }
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
