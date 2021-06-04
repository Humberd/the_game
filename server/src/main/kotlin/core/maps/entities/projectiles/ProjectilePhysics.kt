package core.maps.entities.projectiles

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import core.maps.entities.Collider
import core.maps.entities.CollisionCategory
import core.maps.entities.Lifecycle
import ktx.box2d.box
import ktx.box2d.filter
import mu.KLogging

class ProjectilePhysics(private val thisProjectile: Projectile) : Lifecycle, Collider.WithAnything {
    companion object : KLogging()

    lateinit var body: Body

    override fun onInit() {
        body = thisProjectile.context.create(BodyDef.BodyType.KinematicBody) {
            position.set(thisProjectile.seed.position)
            angle = thisProjectile.seed.rotation

            box(
                width = 1f,
                height = 0.1f,
//                position = thisProjectile.seed.position,
//                angle = thisProjectile.seed.rotation
            ) {
                density = 0f
                friction = 0f
                restitution = 0f
                filter {
                    categoryBits = CollisionCategory.PROJECTILE.value
                    maskBits = CollisionCategory.PROJECTILE.collidesWith()
                }
                userData = this@ProjectilePhysics
            }
        }
    }

    override fun onUpdate(deltaTime: Float) {

    }

    override fun onDestroy() {
        thisProjectile.context.destroy(body)
    }

    override fun onCollisionStart(entity: Any) {
        logger.info { "Projectile Collision start with $entity" }
    }

    override fun onCollisionEnd(entity: Any) {
        logger.info { "Projectile Collision end with $entity" }

    }
}
