package core.maps.entities.creatures

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import core.types.WorldPosition
import ktx.box2d.body
import ktx.box2d.circle

class CreaturePhysics(
    private val thisCreature: Creature
) {
    lateinit var fixture: Fixture
        private set

    fun onInit(position: WorldPosition, bodyRadius: Float) {
        // https://www.aurelienribon.com/post/2011-07-box2d-tutorial-collision-filtering
        val body = thisCreature.gameMap.physics.body(BodyDef.BodyType.DynamicBody) {
            this.position.set(position)
            userData = thisCreature

            circle(radius = bodyRadius) {
                density = 0f
                friction = 0f
                restitution = 0f
                filter.categoryBits = thisCreature.collisionCategory.value
                filter.maskBits = thisCreature.collisionCategory.collidesWith()
                userData = thisCreature
            }
        }
        fixture = body.fixtureList[0]
    }
}
