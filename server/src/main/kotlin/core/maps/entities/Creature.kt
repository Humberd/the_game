package core.maps.entities

import core.maps.GameMapController
import core.types.CID
import core.types.CreatureName
import core.types.SpriteId
import core.types.WorldPosition

abstract class Creature(
    val cid: CID,
    val name: CreatureName,
    val health: UInt,
    val spriteId: SpriteId,
    var position: WorldPosition
) {
    var movementSpeed = 3f
    val viewRadius: UByte = 3u

    val lastUpdate: LastUpdate

    lateinit var gameMapController: GameMapController

    init {
        lastUpdate = LastUpdate(
            gridPosition = GameMap.toGridPosition(position),
            tileSlice = emptyArray()
        )
    }

    data class LastUpdate(
        var gridPosition: GameMap.GridPosition,
        var tileSlice: Array<Array<Tile>>
    )

    fun getVisibleItems(): List<Item> {
        val buffer = arrayListOf<Item>()

        lastUpdate.tileSlice.forEach {
            it.forEach {
                it.writeItemsTo(buffer)
            }
        }

        return buffer
    }

    fun getVisibleCreatures(): List<Creature> {
        val buffer = arrayListOf<Creature>()

        lastUpdate.tileSlice.forEach {
            it.forEach {
                it.writeCreaturesTo(buffer)
            }
        }

        return buffer.filter { it.cid != cid }
    }

    fun getVisiblePlayers(): List<Player> {
        return getVisibleCreatures().filter { it is Player } as List<Player>
    }
}
