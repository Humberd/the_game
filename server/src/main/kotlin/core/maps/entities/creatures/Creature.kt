package core.maps.entities.creatures

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import core.StateChangeNotifier
import core.maps.entities.*
import core.types.*
import utils.getDistance
import utils.toGridPosition

abstract class Creature(
    creatureSeed: CreatureSeed,
    protected val gameMap: GameMap,
    val notifier: StateChangeNotifier
) {
    //region Properties
    val cid = CID.unique()

    var name: CreatureName = creatureSeed.name
        private set

    var experience: Experience = creatureSeed.experience
        private set

    var spriteId: SpriteId = creatureSeed.spriteId
        private set

    val position: WorldPosition
        get() = fixture.body.position

    var tilesViewRadius: TileRadius = creatureSeed.tilesViewRadius
        private set

    val bodyRadius: Float
        get() = fixture.shape.radius
    //endregion

    //region LastUpdate
    val lastUpdate: LastUpdate

    data class LastUpdate(
        var gridPosition: GridPosition,
        var tileSlice: Array<Array<Tile>>
    )

    init {
        val gridPosition = toGridPosition(creatureSeed.position)
        lastUpdate = LastUpdate(
            gridPosition = gridPosition,
            tileSlice = gameMap.getTilesAround(gridPosition, tilesViewRadius.value)
        )
        gameMap.getTileAt(gridPosition).creatures.put(cid, this)
    }
    //endregion

    //region Physics Definition
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

        // https://www.aurelienribon.com/post/2011-07-box2d-tutorial-collision-filtering

        val fixtureDef = FixtureDef().also {
            it.shape = shape
            it.density = 0f
            it.friction = 0f
            it.restitution = 0f
            it.filter.categoryBits = collisionCategory().value
            it.filter.maskBits = collisionCategory().collidesWith()
        }

        fixture = body.createFixture(fixtureDef).also {
            it.userData = this@Creature
        }
        shape.dispose()
    }
    //endregion

    abstract fun initHooks(): CreatureHooks
    val hooks = initHooks()
    abstract fun collisionCategory(): CollisionCategory

    //region Creatures Visibility Cache
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
    //endregion

    //region Movement
    protected var targetPosition: WorldPosition? = null

    fun isMoving(): Boolean {
        return targetPosition != null
    }

    fun stopMoving() {
        targetPosition = null
        fixture.body.setLinearVelocity(0f, 0f)
    }

    fun startMovingTo(targetPosition: WorldPosition) {
        val velocity = targetPosition.cpy().sub(position).nor()
        velocity.x *= stats.movementSpeed.current
        velocity.y *= stats.movementSpeed.current
        this.targetPosition = targetPosition
        fixture.body.setLinearVelocity(velocity)
    }
    //endregion

    fun afterPhysicsUpdate(deltaTime: Float) {
        if (!isMoving()) {
            return
        }

        val distanceToStopMoving = deltaTime * stats.movementSpeed.current
        val currentDistance = getDistance(position, targetPosition!!)
        if (distanceToStopMoving > currentDistance) {
            stopMoving()
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

            [1, 2] -> Creature Disappear
            [3, 4] -> Creature Position Update
            [5, 6] -> Creature Appear
             */

            // Creature Disappear
            ArrayList(creaturesThatSeeMe).forEach {
                if (!it.canSee(this)) {
                    it.creaturesISee.unregister(this)
                }
            }
            creaturesISee.getAll().forEach {
                if (!canSee(it)) {
                    creaturesISee.unregister(it)
                }
            }

            // Creature Appear
            gameMap.creatures.getAllCreatures()
                .filter { it.cid != cid }
                .filter { it.canSee(this) }
                .subtract(creaturesThatSeeMe)
                .forEach {
                    it.creaturesISee.register(this)
                }
            getGreedyVisibleCreatures()
                .subtract(creaturesISee.getAll())
                .forEach {
                    creaturesISee.register(it)
                }

        }

        hooks.onMoved(tileChanged)
        creaturesThatSeeMe.forEach {
            it.hooks.onOtherCreaturePositionChange(this)
        }
    }

    fun getVisibleItems(): List<GameMapObject> {
        val buffer = arrayListOf<GameMapObject>()

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

    fun canSee(otherCreature: Creature): Boolean {
        return getGreedyVisibleCreatures().contains(otherCreature)
    }

    val stats = CreatureStats(this)
    val combat = CreatureCombat(this)
    val equipment = CreatureEquipment(this)

    init {
        creatureSeed.equipment.forEach { type, item ->
            type.getSlot(equipment).equip(item)
        }
    }
}
