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
    abstract val hooks: CreatureHooks

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
        gameMap.getTileAt(gridPosition).creatures.put(cid, this)
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

    val creaturesThatSeeMe = HashSet<Creature>()
    val creaturesISee = VisibleCreatures()

    inner class VisibleCreatures {
        private val set = HashSet<Creature>()

        fun register(creature: Creature) {
            if (set.contains(creature)) {
                throw Error("Creature already exists")
            }

            set.add(creature)
            creature.creaturesThatSeeMe.add(this@Creature)

            hooks.onOtherCreatureAppearInViewRange(creature)
        }

        fun unregister(creature: Creature) {
            if (!set.contains(creature)) {
                throw Error("Creature doesn't exist")
            }

            set.remove(creature)
            creature.creaturesThatSeeMe.remove(this@Creature)

            hooks.onOtherCreatureDisappearFromViewRange(creature)
        }

        fun getAll(): Collection<Creature> {
            return ArrayList(set)
        }
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

        val testXOutsideMap = position.x.coerceIn(0f, gameMap.gridWidth.toFloat())
        val testYOutsideMap = position.y.coerceIn(0f, gameMap.gridHeight.toFloat())
        val testOutsideMap = WorldPosition(testXOutsideMap, testYOutsideMap)
        if (position != testOutsideMap) {
            fixture.body.setTransform(testOutsideMap, 0f)
            stopMoving()
        } else {
            val distanceToStopMoving = deltaTime * velocity
            val currentDistance = getDistance(position, targetPosition!!)
            if (distanceToStopMoving > currentDistance) {
                stopMoving()
            }
        }

        // update tiles in grid
        val oldGridCoords = lastUpdate.gridPosition
        val newGridCoords = toGridPosition(position)
        val tileChanged = oldGridCoords != newGridCoords
        if (tileChanged) {
            lastUpdate.gridPosition = newGridCoords
            lastUpdate.tileSlice = gameMap.getTilesAround(newGridCoords, tilesViewRadius.value)
            gameMap.getTileAt(oldGridCoords).creatures.transferTo(cid, this, gameMap.getTileAt(newGridCoords).creatures)

            /*
            [1,2,3,4] -> [3,4,5,6]

            [1, 2] -> Disappear
            [3, 4] -> Creature Position Update
            [5, 6] -> Creature Update
             */


        }

        hooks.onMoved()
        creaturesThatSeeMe.forEach {
            it.hooks.onOtherCreaturePositionChange(this)
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

    fun getGreedyVisibleCreatures(): List<Creature> {
        val buffer = arrayListOf<Creature>()

        lastUpdate.tileSlice.forEach {
            it.forEach {
                it.creatures.writeTo(buffer)
            }
        }

        return buffer.filter { it.cid != cid }
    }

    fun getVisiblePlayers(): List<Player> {
        return getGreedyVisibleCreatures().filter { it is Player } as List<Player>
    }

    fun canSee(otherCreature: Creature): Boolean {
        return getGreedyVisibleCreatures().contains(otherCreature)
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
