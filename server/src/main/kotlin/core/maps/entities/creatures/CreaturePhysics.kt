package core.maps.entities.creatures

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import core.types.WorldPosition
import ktx.box2d.body
import ktx.box2d.circle

class CreaturePhysics(
    private val thisCreature: Creature
) {
    lateinit var body: Body
        private set

    fun onInit(position: WorldPosition) {
        // https://www.aurelienribon.com/post/2011-07-box2d-tutorial-collision-filtering
        body = thisCreature.gameMap.physics.body(BodyDef.BodyType.DynamicBody) {
            this.position.set(position)
            userData = thisCreature
        }
        body.circle(radius = thisCreature.bodyRadius) {
            density = 0f
            friction = 0f
            restitution = 0f
            filter.categoryBits = thisCreature.collisionCategory.value
            filter.maskBits = thisCreature.collisionCategory.collidesWith()
            userData = thisCreature
        }
    }

    fun onDestroy() {
        thisCreature.gameMap.physics.destroyBody(body)
    }
}
