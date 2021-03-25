package core.maps.entities.creatures

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import core.types.WorldPosition

class CreaturePhysics(
    private val thisCreature: Creature
) {
    lateinit var fixture: Fixture
        private set

    fun onInit(position: WorldPosition, bodyRadius: Float) {
        val bodyDef = BodyDef().also {
            it.type = BodyDef.BodyType.DynamicBody
            it.position.set(position)
        }

        val body = thisCreature.gameMap.physics.createBody(bodyDef)

        val shape = CircleShape().also {
            it.radius = bodyRadius
        }

        // https://www.aurelienribon.com/post/2011-07-box2d-tutorial-collision-filtering

        val fixtureDef = FixtureDef().also {
            it.shape = shape
            it.density = 0f
            it.friction = 0f
            it.restitution = 0f
            it.filter.categoryBits = thisCreature.collisionCategory.value
            it.filter.maskBits = thisCreature.collisionCategory.collidesWith()
        }

        fixture = body.createFixture(fixtureDef).also {
            it.userData = thisCreature
        }
        shape.dispose()
    }
}
