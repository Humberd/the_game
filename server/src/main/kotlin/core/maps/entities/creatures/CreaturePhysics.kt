package core.maps.entities.creatures

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import core.types.WorldPosition
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.weldJointWith
import mu.KLogging

class CreaturePhysics(
    private val thisCreature: Creature
) {
    companion object : KLogging()

    private lateinit var bodySensor: Body
    lateinit var body: Body
        private set

    fun onInit(position: WorldPosition) {
        // https://www.aurelienribon.com/post/2011-07-box2d-tutorial-collision-filtering
        body = thisCreature.gameMap.physics.body(BodyDef.BodyType.KinematicBody) {
            this.position.set(position)
        }

        bodySensor = thisCreature.gameMap.physics.body(BodyDef.BodyType.DynamicBody) {
            this.position.set(position)

            circle(radius = thisCreature.bodyRadius) {
                density = 0f
                friction = 0f
                restitution = 0f
                filter.categoryBits = thisCreature.collisionCategory.value
                filter.maskBits = thisCreature.collisionCategory.collidesWith()
                userData = thisCreature
            }
        }.also { body.weldJointWith(it) }
    }

    fun onDestroy() {
        thisCreature.gameMap.physics.also {
            it.destroyBody(body)
            it.destroyBody(bodySensor)
        }
    }

    fun joinWithMainBody(body: Body) {
        this.body.weldJointWith(body)
    }
}
