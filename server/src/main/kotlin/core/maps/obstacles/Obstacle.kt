package core.maps.obstacles

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import core.maps.entities.CollisionCategory
import core.maps.entities.GameContext
import ktx.box2d.loop

class Obstacle(
    val path: List<Vector2>
) {
    private lateinit var body: Body

    fun onInit(context: GameContext) {
        body = context.create {
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

    fun onDestroy(context: GameContext) {
        context.destroy(body)
    }
}
