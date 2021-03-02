package core.maps

import core.types.Coordinate
import core.types.GameMapId
import core.types.SpriteId
import org.mini2Dx.gdx.math.Vector2

private const val GRAVEL_SPRITE: UShort = 0u
private const val GRASS_SPRITE: UShort = 1u

object GameMapGenerator {
    fun generateMap1(width: Int, height: Int): GameMap {
        val grid = Array(width) { x ->
            Array(height) { y ->
                GameMap.Tile(
                    spriteId = SpriteId(if (x % 4 == 0) GRAVEL_SPRITE else GRASS_SPRITE),
                    gridPosition = GameMap.GridPosition(Coordinate(x), Coordinate(y))
                )
            }
        }

        return GameMap(
            id = GameMapId(1u),
            gridWidth = width,
            gridHeight = height,
            worldOffset = Vector2.Zero,
            grid = grid
        )
    }
}
