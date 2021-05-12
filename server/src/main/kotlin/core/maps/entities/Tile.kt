package core.maps.entities

import com.badlogic.gdx.physics.box2d.World
import core.maps.obstacles.Obstacle
import core.types.GridPosition
import core.types.SpriteId

data class Tile(
    val spriteId: SpriteId,
    val gridPosition: GridPosition,
    val obstacles: List<Obstacle> = emptyList()
) {
    fun onInit(physics: World) {
        obstacles.forEach { it.onInit(physics) }
    }
}
