package core.maps.entities

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import core.maps.shapes.Wall
import core.types.*
import mu.KotlinLogging
import utils.toGridPosition

private val logger = KotlinLogging.logger {}

class GameMap(
    val id: GameMapId,
    val gridWidth: Int,
    val gridHeight: Int,
    private val grid: Array<Array<Tile>>,
    items: List<Item>,
) {
    //region Creatures Store
    val creatures = CreaturesContainer()

    inner class CreaturesContainer {
        private val players = HashMap<PID, Player>()
        private val creatures = HashMap<CID, Creature>()

        fun add(creature: Creature) {
            if (creatures.containsKey(creature.cid)) {
                throw Error("Creature already exists")
            }

            if (creature is Player) {
                if (players.containsKey(creature.pid)) {
                    throw Error("Player already exists")
                }
                players[creature.pid] = creature
            }

            creatures[creature.cid] = creature

            creature.hooks.onAddedToMap(this@GameMap)
        }

        fun remove(pid: PID) {
            if (!players.containsKey(pid)) {
                throw Error("Player doesn't exist")
            }

            remove(players[pid]!!.cid)
            players.remove(pid)
        }

        fun remove(cid: CID) {
            if (!creatures.containsKey(cid)) {
                throw Error("Creature doesn't exist")
            }
            val removedCreature = creatures.remove(cid)
            removedCreature!!.hooks.onRemovedFromMap(this@GameMap)
        }

        fun get(pid: PID): Player {
            return players[pid] ?: throw Error("Player not found")
        }

        fun get(cid: CID): Creature {
            return creatures[cid] ?: throw Error("Creature not found")
        }

        fun getAllPlayers(): Collection<Player> {
            return players.values
        }

        fun getAllCreatures(): Collection<Creature> {
            return creatures.values
        }

        fun moveTo(pid: PID, targetPosition: WorldPosition) {
            moveTo(get(pid).cid, targetPosition)
        }

        fun moveTo(cid: CID, targetPosition: WorldPosition) {
            val creature = get(cid)
            creature.startMovingTo(targetPosition)
        }
    }
    //endregion

    //region Physics Initialization
    val physics: World

    init {
        val gravity = Vector2(0f, 0f)
        physics = World(gravity, true)
        initMapBounds()
//        GameMapDebugRenderer(this)
        physics.setContactListener(GameMapContactListener())
    }

    private fun initMapBounds() {
        val vertices = listOf(
            Pair(Vector2(0f, 0f), gridWidth),
            Pair(Vector2(gridWidth.toFloat(), 0f), gridHeight),
            Pair(Vector2(gridWidth.toFloat(), gridHeight.toFloat()), gridWidth),
            Pair(Vector2(0f, gridHeight.toFloat()), gridHeight),
        )

        vertices.forEachIndexed { index, pair ->
            val width = pair.second
            val startingPosition = pair.first

            val bodyDef = BodyDef().also {
                it.type = BodyDef.BodyType.StaticBody
            }

            val shape = EdgeShape().also {
                it.set(Vector2(0f, 0f), Vector2(width.toFloat(), 0f))
            }

            val fixtureDef = FixtureDef().also {
                it.shape = shape
                it.density = 0f
                it.friction = 0f
                it.restitution = 0f
                it.filter.categoryBits = CollisionCategory.TERRAIN.value
                it.filter.maskBits = CollisionCategory.TERRAIN.collidesWith()
            }

            physics.createBody(bodyDef).also {
                it.createFixture(fixtureDef).also {
                    it.userData = Wall()
                }
                it.setTransform(startingPosition, 90 * MathUtils.degreesToRadians * index)
            }

            shape.dispose()
        }
    }

    //endregion

    fun onPhysicsStep(deltaTime: Float) {
        val velocityIterations = 6
        val positionIterations = 2
        physics.step(deltaTime, velocityIterations, positionIterations)

        creatures.getAllCreatures().forEach {
            it.afterPhysicsUpdate(deltaTime)
        }
    }

    //region Combat
    fun startAttacking(pid: PID, targetCID: CID) {
        val sourceCreature = creatures.get(pid)
        val targetCreature = creatures.get(targetCID)

        sourceCreature.combat.startAttacking(targetCreature)
    }


    fun stopAttacking(pid: PID) {
        val sourceCreature = creatures.get(pid)

        sourceCreature.combat.stopAttacking()
    }
    //endregion

    //region Tile Utils
    fun getTilesAround(position: GridPosition, radius: Int): Array<Array<Tile>> {
        require(radius >= 0)
        val locX = position.x.value
        val locY = position.y.value

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

        val arrWidth = (endX - startX + 1).also {
            if (it <= 0) return emptyArray()
        }
        val arrHeight = (endY - startY + 1).also {
            if (it <= 0) return emptyArray()
        }
        val result = Array<Array<Tile?>>(arrWidth) {
            Array(arrHeight) {
                null
            }
        }

//        println("$startX, $startY -> $endX, $endY")

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

    fun getTileFor(creature: Creature): Tile {
        return getTileAt(toGridPosition(creature.position))
    }

    fun getTileFor(item: Item): Tile {
        return getTileAt(toGridPosition(item.position))
    }
    //endregion
}
