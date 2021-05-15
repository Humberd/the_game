package core.maps.entities

import core.maps.obstacles.Obstacle
import core.types.GridPosition
import core.types.SpriteId

data class Tile(
    val spriteId: SpriteId,
    val gridPosition: GridPosition,
    val obstacles: List<Obstacle> = emptyList()
) {
    fun onInit(context: GameContext) {
        obstacles.forEach { it.onInit(context) }
    }

    fun onDestroy(context: GameContext) {
        obstacles.forEach { it.onDestroy(context) }
    }
}
