package core.maps.entities.projectiles

import core.maps.entities.Entity
import core.maps.entities.GameContext

class Projectile(
    val seed: ProjectileSeed,
    val context: GameContext
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
