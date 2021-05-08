package core.maps.obstacles

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import core.maps.entities.CollisionCategory
import ktx.box2d.body
import ktx.box2d.loop

class Obstacle(
    val path: List<Vector2>
) {
    fun onInit(physics: World) {
        physics.body {
            userData = this@Obstacle

            loop(*path.toTypedArray()) {
                userData = this@Obstacle
                density = 0f
                friction = 0f
                restitution = 0f
                filter.categoryBits = CollisionCategory.TERRAIN.value
                filter.maskBits = CollisionCategory.TERRAIN.collidesWith()
            }
        }
    }
}
