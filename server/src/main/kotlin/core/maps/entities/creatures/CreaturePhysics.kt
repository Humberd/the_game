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
    lateinit var body: Body
        private set

    fun onInit(position: WorldPosition) {
        // https://www.aurelienribon.com/post/2011-07-box2d-tutorial-collision-filtering
        body = thisCreature.gameMap.physics.body(BodyDef.BodyType.KinematicBody) {
            this.position.set(position)
            userData = thisCreature
        }

        val sensor = thisCreature.gameMap.physics.body(BodyDef.BodyType.DynamicBody) {
            this.position.set(position)
            userData = thisCreature

            box(
                width = thisCreature.tilesViewRadius.value.toFloat(),
                height = thisCreature.tilesViewRadius.value.toFloat()
            ) {
                density = 0f
                friction = 0f
                restitution = 0f
                filter.categoryBits = CollisionCategory.DETECTION.value
                filter.maskBits = CollisionCategory.DETECTION.collidesWith()
                isSensor = true
                userData = thisCreature
            }
        }

        val sensor2 = thisCreature.gameMap.physics.body(BodyDef.BodyType.DynamicBody) {
            this.position.set(position)
            userData = thisCreature

            circle(radius = thisCreature.bodyRadius) {
                density = 0f
                friction = 0f
                restitution = 0f
                filter.categoryBits = thisCreature.collisionCategory.value
                filter.maskBits = thisCreature.collisionCategory.collidesWith()
                userData = thisCreature
            }
        }

        body.weldJointWith(sensor) {

        }

        body.weldJointWith(sensor2) {

        }


    }

    fun onDestroy() {
        thisCreature.gameMap.physics.destroyBody(body)
    }
}
