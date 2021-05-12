package core.maps.entities.creatures

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import core.maps.entities.Collider
import core.maps.entities.CollisionCategory
import ktx.box2d.body
import ktx.box2d.box

class CreatureFovSensor(private val thisCreature: Creature) : Collider.WithCreature {
    private lateinit var tileViewSensor: Body

    fun onInit() {
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
                userData = this@CreatureFovSensor
            }
        }.also { thisCreature.physics.joinWithMainBody(it) }
    }

    fun onDestroy() {
        thisCreature.gameMap.physics.destroyBody(tileViewSensor)
    }

    override fun onCollisionStart(entity: Creature) {
        if (entity === thisCreature) {
            return
        }
        CreaturePhysics.logger.info { "Collision start with $entity" }
    }

    override fun onCollisionEnd(entity: Creature) {
        CreaturePhysics.logger.info { "Collision end with $entity" }
    }
}
