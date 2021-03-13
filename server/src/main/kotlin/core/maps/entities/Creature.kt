package core.maps.entities

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import core.StateChangeNotifier
import core.types.*
import infrastructure.udp.egress.EgressDataPacket
import utils.getDistance
import utils.toGridPosition

data class CreatureSeed(
    val name: CreatureName,
    val baseHealth: UInt,
    val currentHealth: UInt,
    val spriteId: SpriteId,
    val position: WorldPosition,
    val velocity: Float,
    val tilesViewRadius: TileRadius,
    val bodyRadius: Float
)

abstract class Creature(
    creatureSeed: CreatureSeed,
    protected val gameMap: GameMap,
    protected val notifier: StateChangeNotifier
) {
    val cid = CID.unique()

    var name: CreatureName = creatureSeed.name
        private set

    var baseHealth: UInt = creatureSeed.baseHealth
        private set

    var currentHealth: UInt = creatureSeed.currentHealth
        private set

    var spriteId: SpriteId = creatureSeed.spriteId
        private set

    val position: WorldPosition
        get() = fixture.body.position

    var velocity: Float = creatureSeed.velocity
        private set

    var tilesViewRadius: TileRadius = creatureSeed.tilesViewRadius
        private set

    val bodyRadius: Float
        get() = fixture.shape.radius

    val lastUpdate: LastUpdate

    init {
        val gridPosition = toGridPosition(creatureSeed.position)
        lastUpdate = LastUpdate(
            gridPosition = gridPosition,
            tileSlice = gameMap.getTilesAround(gridPosition, tilesViewRadius.value)
        )
    }

    val fixture: Fixture

    init {
        val bodyDef = BodyDef().also {
            it.type = BodyDef.BodyType.DynamicBody
            it.position.set(creatureSeed.position)
        }

        val body = gameMap.physics.createBody(bodyDef)

        val shape = CircleShape().also {
            it.radius = creatureSeed.bodyRadius
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
            hooks.onMoved()
        }
    }

    data class LastUpdate(
        var gridPosition: GridPosition,
        var tileSlice: Array<Array<Tile>>
    )

    abstract fun onOtherCreatureDisappearFromViewRange(otherCreature: Creature)
    abstract fun onOtherCreatureAppearInViewRange(otherCreature: Creature)
    abstract fun onOtherCreaturePositionChange(otherCreature: Creature)

    fun getVisibleItems(): List<Item> {
        val buffer = arrayListOf<Item>()

        lastUpdate.tileSlice.forEach {
            it.forEach {
                it.items.writeTo(buffer)
            }
        }

        return buffer
    }

    fun getVisibleCreatures(): List<Creature> {
        val buffer = arrayListOf<Creature>()

        lastUpdate.tileSlice.forEach {
            it.forEach {
                it.creatures.writeTo(buffer)
            }
        }

        return buffer.filter { it.cid != cid }
    }

    fun getVisiblePlayers(): List<Player> {
        return getVisibleCreatures().filter { it is Player } as List<Player>
    }

    fun takeDamage(damage: UInt) {
        val newHealth = currentHealth.toInt() - damage.toInt()

        if (newHealth <= 0) {
            currentHealth = 0u
            // call dead hook
        } else {
            currentHealth = newHealth.toUInt()
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
        return currentHealth == 0u
    }
}
