package core.maps.entities.projectiles

import core.maps.entities.Entity
import core.maps.entities.GameContextImpl

class Projectile(
    val seed: ProjectileSeed,
    val context: GameContextImpl
) : Entity {
    private val physics = ProjectilePhysics(this)

    override fun onInit() {
        physics.onInit()
    }

    override fun onUpdate(deltaTime: Float) {
        physics.onUpdate(deltaTime)
    }

    override fun onDestroy() {
        physics.onDestroy()
    }
}
