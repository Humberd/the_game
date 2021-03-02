package core.maps

import core.types.Coordinate
import core.types.GameMapId
import core.types.SpriteId
import core.types.WorldPosition
import kotlin.math.ceil


class GameMap(
    val id: GameMapId,
    val gridWidth: Int,
    val gridHeight: Int,
    val worldOffset: WorldPosition,
    private val grid: Array<Array<Tile>>
) {
    val TILE_SIZE = 64

    data class Tile(
        val spriteId: SpriteId,
        val gridPosition: GridPosition
    )

    data class GridPosition(
        val x: Coordinate,
        val y: Coordinate
    )

    fun getTilesAround(position: GridPosition, radius: Int): Array<Tile> {
        require(radius >= 0)
        val locX = position.x.value
        val locY = position.y.value

        val result = ArrayList<Tile>(radius * 2 + 1)

        val startX = (locX - radius).let {
            if (it < 0) 0 else it
        }
        val endX = (locX + radius).let {
            if (it >= gridWidth) gridWidth - 1 else it
        }
        val startY = (locY - radius).let {
            if (it < 0) 0 else it
        }
        val endY = (locY + radius).let {
            if (it >= gridHeight) gridHeight - 1 else it
        }

        for (x in startX..endX) {
            for (y in startY..endY) {
                result.add(grid[x][y])
            }
        }

        return result.toTypedArray()
    }

    fun toGridPosition(position: WorldPosition): GridPosition {
        return GridPosition(
            x = Coordinate(ceil(((position.x + worldOffset.x) / TILE_SIZE)).toInt()),
            y = Coordinate(ceil(((position.y + worldOffset.y) / TILE_SIZE)).toInt())
        )
    }
}
