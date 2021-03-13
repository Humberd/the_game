package core.maps.entities

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import core.StateChangeNotifier
import core.maps.GameMapController
import core.types.*
import infrastructure.udp.egress.EgressDataPacket
import utils.getDistance

abstract class Creature(
    val cid: CID,
    val name: CreatureName,
    var health: UInt,
    val spriteId: SpriteId,
) {
    val velocity = 3f
    val tilesViewRadius = TileRadius(3)

    val lastUpdate: LastUpdate

    lateinit var fixture: Fixture

    open val scriptable = ScriptableCreature()

    protected lateinit var gameMapController: GameMapController
    protected lateinit var notifier: StateChangeNotifier

    val position: WorldPosition
        get() = fixture.body.position
    var targetPosition: WorldPosition? = null

    fun isMoving(): Boolean {
        return targetPosition != null
    }

    fun stopMoving() {
        targetPosition = null
        fixture.body.setLinearVelocity(0f, 0f)
    }

    fun startMovingTo(targetPosition: WorldPosition) {
        val velocity = targetPosition.cpy().sub(position).nor()
        velocity.x *= this.velocity
        velocity.y *= this.velocity
        this.targetPosition = targetPosition
        fixture.body.setLinearVelocity(velocity)
    }

    fun afterPhysicsUpdate(deltaTime: Float) {
        if (!isMoving()) {
            return
        }

        val distanceToStopMoving = deltaTime * velocity
        val currentDistance = getDistance(position, targetPosition!!)
        if (distanceToStopMoving > currentDistance) {
            stopMoving()
        }

        if (this is Player) {
            notifier.notifyCreatureUpdate(pid, this)
        }
    }


    init {
        lastUpdate = LastUpdate(
            gridPosition = GameMap.toGridPosition(WorldPosition(0f, 0f)),
            tileSlice = emptyArray()
        )
    }

    fun createPhysicsBody(world: World) {
        val bodyDef = BodyDef().also {
            it.type = BodyDef.BodyType.DynamicBody
            it.position.set(0f, 0f)
        }

        val body = world.createBody(bodyDef)

        val shape = CircleShape().also {
            it.radius = 0.5f
        }

        val fixtureDef = FixtureDef().also {
            it.shape = shape
            it.density = 0f
            it.friction = 0f
            it.restitution = 0f
        }

        fixture = body.createFixture(fixtureDef)

        shape.dispose()
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
//            gameMapController.moveBy(cid, vector)
        }

        fun moveTo(targetPosition: WorldPosition) {
//            gameMapController.moveTo(this@Creature, targetPosition)
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
