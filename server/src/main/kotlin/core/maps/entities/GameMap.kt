package core.maps.entities

import core.maps.entities.creatures.Creature
import core.types.GridPosition
import utils.toGridPosition

class GameMap(
    seed: GameMapSeed,
    private val context: GameContext
) : Entity {
    val grid = seed.grid
    val gridWidth = grid.size
    val gridHeight = grid[0].size
    val navigation = NavigationBuilder.buildMapNavigation(seed.geometryProvider)

    override fun onInit() {
        grid.forEach { it.forEach { it.onInit(context) } }
    }

    override fun onDestroy() {
        grid.forEach { it.forEach { it.onDestroy(context) } }
    }

    fun getTilesAround(position: GridPosition, radius: Int): Array<Array<Tile>> {
        require(radius >= 0)
        val locX = position.x.value
        val locY = position.y.value

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

        val arrWidth = (endX - startX + 1).also {
            if (it <= 0) return emptyArray()
        }
        val arrHeight = (endY - startY + 1).also {
            if (it <= 0) return emptyArray()
        }
        val result = Array<Array<Tile?>>(arrWidth) {
            Array(arrHeight) {
                null
            }
        }

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
}
