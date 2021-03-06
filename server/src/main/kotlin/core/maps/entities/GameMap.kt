package core.maps.entities

import core.types.Coordinate
import core.types.GameMapId
import core.types.WorldPosition
import kotlin.math.floor


class GameMap(
    val id: GameMapId,
    val gridWidth: Int,
    val gridHeight: Int,
    private val grid: Array<Array<Tile>>,
    val items: List<Item>,
    val creatures: List<Creature>
) {
    companion object {
        val TILE_SIZE = 64

        fun toGridPosition(position: WorldPosition): GridPosition {
            return GridPosition(
                x = Coordinate(floor(((position.x) / TILE_SIZE)).toInt()),
                y = Coordinate(floor(((position.y) / TILE_SIZE)).toInt())
            )
        }
    }

    init {
        items.forEach {
            val (x, y) = toGridPosition(it.position)
            grid[x.value][y.value].putItem(it)
        }

        creatures.forEach {
            val (x, y) = toGridPosition(it.position)
            grid[x.value][y.value].putCreature(it)
        }
    }

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

    fun getTileAt(coords: GridPosition): Tile {
        val (x, y) = coords

        if (x.value >= gridWidth || y.value >= gridHeight) {
            throw Error("Cannot get tile at ${coords}")
        }

        return grid[x.value][y.value]
    }

    fun getTileFor(creature: Creature): Tile {
        return getTileAt(toGridPosition(creature.position))
    }

    fun getTileFor(item: Item): Tile {
        return getTileAt(toGridPosition(item.position))
    }
}
