package core.maps.entities

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import core.AsyncGameTask
import core.GameLoop
import core.StateChangeNotifier
import core.types.*
import infrastructure.udp.egress.EgressDataPacket
import utils.getDistance
import utils.ms
import utils.sec
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
    //region Properties
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

    abstract val hooks: CreatureHooks
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
        velocity.x *= this.velocity
        velocity.y *= this.velocity
        this.targetPosition = targetPosition
        fixture.body.setLinearVelocity(velocity)
    }
    //endregion

    fun afterPhysicsUpdate(deltaTime: Float) {
        if (!isMoving()) {
            return
        }

        val distanceToStopMoving = deltaTime * velocity
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

    fun canSee(otherCreature: Creature): Boolean {
        return getGreedyVisibleCreatures().contains(otherCreature)
    }

    //region Combat
    var combat = Combat()

    inner class Combat {
        var attackedTarget: Creature? = null
            private set
        val attackedByTargets: MutableSet<Creature> = mutableSetOf()
        private var attackTask: AsyncGameTask? = null

        fun takeDamage(damage: UInt) {
            val newHealth = currentHealth.toInt() - damage.toInt()

            if (newHealth <= 0) {
                currentHealth = 0u
                die()
            } else {
                currentHealth = newHealth.toUInt()
            }

            hooks.onSelfDamageTaken(damage)
            creaturesThatSeeMe.forEach { it.hooks.onOtherCreatureDamageTaken(this@Creature, damage) }
        }

        private fun die() {
            attackedByTargets.forEach { it.combat.stopAttacking() }
            hooks.onDeath()
        }

        fun startAttacking(target: Creature) {
            if (isCurrentlyAttacking()) {
                if (target === attackedTarget) {
                    throw IllegalStateException("Cannot attack the same target again")
                }

                stopAttacking()
            }

            if (!canSee(target)) {
                throw Error("Creature can't see the target")
            }

            attackedTarget = target
            target.combat.attackedByTargets.add(this@Creature)

            val attackSpeed = 1000.ms
            val damage = 10u

            val projectileUnitsPerSecond = 3f

            attackTask = GameLoop.instance.requestAsyncTask(attackSpeed) {
                val distanceToTarget = getDistance(position, target.position)
                val projectileDelay = (distanceToTarget.toFloat() / projectileUnitsPerSecond).sec

                // FIXME: 16.03.2021 Should be item hook: `onItemUsed` or something like that
                if (this@Creature is Player) {
                    notifier.sendProjectile(
                        pid, EgressDataPacket.ProjectileSend(
                            spriteId = SpriteId(13u),
                            sourcePosition = position,
                            targetPosition = target.position,
                            duration = projectileDelay
                        )
                    )
                }

                GameLoop.instance.requestAsyncTaskOnce(projectileDelay) {
                    target.combat.takeDamage(damage)
                }
            }

            hooks.onStartAttackOtherCreature(target)
            target.hooks.onBeingAttackedBy(this@Creature)
        }

        fun stopAttacking() {
            val task = attackTask
            check(task != null)
            val target = attackedTarget
            check(target != null)

            task.cancel()
            attackedTarget = null
            target.combat.attackedByTargets.remove(this@Creature)
            attackTask = null

            hooks.onStoppedAttackOtherCreature(target)
        }

        fun isCurrentlyAttacking(): Boolean {
            return attackedTarget != null;
        }
    }

    //endregion
}
