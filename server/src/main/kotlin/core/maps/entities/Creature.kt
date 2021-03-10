package core.maps.entities

import core.maps.GameMapController
import core.types.*
import org.mini2Dx.gdx.math.Vector2

abstract class Creature(
    val cid: CID,
    val name: CreatureName,
    val health: UInt,
    val spriteId: SpriteId,
    var position: WorldPosition
) {
    var movementSpeed = 3f
    val tilesViewRadius = TileRadius(3)
    val bodyRadius = WorldRadius(32)

    val lastUpdate: LastUpdate

    open lateinit var scriptable: ScriptableCreature

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

    fun connectWithMap(gameMapController: GameMapController) {
        if (this::scriptable.isInitialized) {
            throw Error("Scriptable object is already connected with a map")
        }

        scriptable = ScriptableCreature(gameMapController)
    }

    inner class ScriptableCreature(
        private val gameMapController: GameMapController
    ) {
        fun moveBy(vector: Vector2) {
            gameMapController.moveBy(cid, vector)
        }

        fun moveTo(targetPosition: WorldPosition) {
            gameMapController.moveTo(this@Creature, targetPosition)
        }
    }

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
