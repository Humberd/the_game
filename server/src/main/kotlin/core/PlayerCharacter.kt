package core

import core.maps.GameMap
import core.types.PID
import core.types.PlayerName
import core.types.WorldPosition
import org.mini2Dx.gdx.math.Vector2

class PlayerCharacter(
    val id: PID,
    val name: PlayerName,
    val health: UInt,
    var position: WorldPosition = Vector2(0f, 0f)
) {
    var movementSpeed = 3f
    val viewRadius: UByte = 3u

    val lastUpdate: LastUpdate;

    init {
        lastUpdate = LastUpdate(
            gridPosition = GameMap.toGridPosition(position),
            tileSlice = emptyArray()
        )
    }

    data class LastUpdate(
        var gridPosition: GameMap.GridPosition,
        var tileSlice: Array<Array<GameMap.Tile>>
    )

    fun getVisibleItems(): List<GameMap.Item> {
        val buffer = arrayListOf<GameMap.Item>()

        lastUpdate.tileSlice.forEach {
            it.forEach {
                it.writeItemsTo(buffer)
            }
        }

        return buffer
    }
}
