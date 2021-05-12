package core.maps.entities.creatures

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import core.maps.entities.CollisionCategory
import core.types.WorldPosition
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.circle
import ktx.box2d.weldJointWith

class CreaturePhysics(
    private val thisCreature: Creature
) {
    private lateinit var bodySensor: Body
    private lateinit var tileViewSensor: Body
    lateinit var body: Body
        private set

    fun onInit(position: WorldPosition) {
        // https://www.aurelienribon.com/post/2011-07-box2d-tutorial-collision-filtering
        body = thisCreature.gameMap.physics.body(BodyDef.BodyType.KinematicBody) {
            this.position.set(position)
        }

        tileViewSensor = thisCreature.gameMap.physics.body(BodyDef.BodyType.DynamicBody) {
            this.position.set(position)

            box(
                width = thisCreature.tilesViewRadius.value.toFloat() * 2,
                height = thisCreature.tilesViewRadius.value.toFloat() * 2
            ) {
                density = 0f
                friction = 0f
                restitution = 0f
                filter.categoryBits = CollisionCategory.DETECTION.value
                filter.maskBits = CollisionCategory.DETECTION.collidesWith()
                isSensor = true
                userData = thisCreature
            }
        }.also { body.weldJointWith(it) }

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
            it.destroyBody(tileViewSensor)
        }
    }
}
