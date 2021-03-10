package core.maps.entities

import core.StateChangeNotifier
import core.maps.GameMapController
import core.types.*
import infrastructure.udp.egress.EgressDataPacket
import org.mini2Dx.gdx.math.Vector2

abstract class Creature(
    val cid: CID,
    val name: CreatureName,
    var health: UInt,
    val spriteId: SpriteId,
    var position: WorldPosition
) {
    var movementSpeed = 3f
    val tilesViewRadius = TileRadius(3)
    val bodyRadius = WorldRadius(32)

    val lastUpdate: LastUpdate

    open val scriptable = ScriptableCreature()

    protected lateinit var gameMapController: GameMapController
    protected lateinit var notifier: StateChangeNotifier


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

    fun connectWithMap(gameMapController: GameMapController, notifier: StateChangeNotifier) {
        if (this::gameMapController.isInitialized) {
            throw Error("Scriptable object is already connected with a map")
        }
        this.gameMapController = gameMapController
        this.notifier = notifier

    }

    inner open class ScriptableCreature {
        fun moveBy(vector: Vector2) {
            gameMapController.moveBy(cid, vector)
        }

        fun moveTo(targetPosition: WorldPosition) {
            gameMapController.moveTo(this@Creature, targetPosition)
        }
    }

    abstract fun onOtherCreatureDisappearFromViewRange(otherCreature: Creature)
    abstract fun onOtherCreatureAppearInViewRange(otherCreature: Creature)
    abstract fun onOtherCreaturePositionChange(otherCreature: Creature)

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

    fun takeDamage(damage: UInt) {
        val newHealth = health.toInt() - damage.toInt()

        if (newHealth <= 0) {
            health = 0u
            // call dead hook
        } else {
            health = newHealth.toUInt()
        }

        if (this is Player) {
            notifier.notifyDamageTaken(
                pid,
                arrayOf(
                    EgressDataPacket.DamageTaken.Damage(position, damage)
                )
            )
        }
    }

    fun isDead(): Boolean {
        return health == 0u
    }
}
