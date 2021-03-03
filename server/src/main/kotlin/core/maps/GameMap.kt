package core.maps

import core.types.Coordinate
import core.types.GameMapId
import core.types.SpriteId
import core.types.WorldPosition
import kotlin.math.ceil
import kotlin.math.floor


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

    fun getTilesAround(position: GridPosition, radius: Int): Array<Array<Tile>> {
        require(radius >= 0)
        val locX = position.x.value
        val locY = position.y.value

        println(position)

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

        val result = Array<Array<Tile?>>(endX - startX + 1) {
            Array(endY - startY + 1) {
                null
            }
        }

        println("$startX, $startY -> $endX, $endY")

        for (x in startX..endX) {
            for (y in startY..endY) {
                result[x - startX][y - startY] = grid[x][y]
            }
        }

        return result as Array<Array<Tile>>
    }

    fun toGridPosition(position: WorldPosition): GridPosition {
        return GridPosition(
            x = Coordinate(floor(((position.x + worldOffset.x) / TILE_SIZE)).toInt()),
            y = Coordinate(floor(((position.y + worldOffset.y) / TILE_SIZE)).toInt())
        )
    }
}
