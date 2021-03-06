package core.maps

import core.types.*
import gameland.ItemActionHandler
import kotlin.math.floor


class GameMap(
    val id: GameMapId,
    val gridWidth: Int,
    val gridHeight: Int,
    val worldOffset: WorldPosition,
    private val grid: Array<Array<Tile>>,
    val items: List<Item>
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
    }

    data class Tile(
        val spriteId: SpriteId,
        val gridPosition: GridPosition,
        private val items: HashMap<InstanceId, Item> = hashMapOf()
    ) {
        fun putItem(item: Item) {
            if (items.containsKey(item.instanceId)) {
                throw Error("Cannot put item to tile for ${item}, because it is already there.")
            }

            items[item.instanceId] = item
        }

        fun removeItem(instanceId: InstanceId) {
            if (!items.containsKey(instanceId)) {
                throw Error("Cannot remove from tile for ${instanceId}, because it's not there")
            }

            items.remove(instanceId)
        }

        fun writeItemsTo(buffer: ArrayList<Item>) {
            items.values.forEach {
                buffer.add(it)
            }
        }

        fun moveItemToTile(item: Item, targetTile: Tile) {
            removeItem(item.instanceId)
            targetTile.putItem(item)
        }

    }

    data class Item(
        val instanceId: InstanceId,
        val itemDef: ItemDef,
        var position: WorldPosition,
        val actionHandler: ItemActionHandler = object : ItemActionHandler {}
    ) {
        fun collidesWith(checkedPosition: WorldPosition): Boolean {
            val ac = Math.abs(position.x - checkedPosition.x)
            val cb = Math.abs(position.y - checkedPosition.y)

            val distance = Math.hypot(ac.toDouble(), cb.toDouble())

            return distance < itemDef.collisionRadius.value.toDouble()
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
}
