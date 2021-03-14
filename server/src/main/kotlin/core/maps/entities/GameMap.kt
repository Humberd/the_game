package core.maps.entities

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import core.maps.shapes.Wall
import core.types.GameMapId
import core.types.GridPosition
import core.types.PID
import core.types.WorldPosition
import mu.KotlinLogging
import utils.toGridPosition

private val logger = KotlinLogging.logger {}

class GameMap(
    val id: GameMapId,
    val gridWidth: Int,
    val gridHeight: Int,
    private val grid: Array<Array<Tile>>,
    items: List<Item>,
    creatures: List<Creature>
) {
    val players = PlayersContainer()

    val physics: World
    val physicsDebug: GameMapDebugRenderer

    init {
        val gravity = Vector2(0f, 0f)
        physics = World(gravity, true)
        initMapBounds()
        physicsDebug = GameMapDebugRenderer(this)
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

    inner class PlayersContainer {
        private val map = HashMap<PID, Player>()

        fun add(player: Player) {
            if (map.containsKey(player.pid)) {
                throw Error("Player already exists")
            }

            map[player.pid] = player
            player.hooks.onAddedToMap(this@GameMap)
        }

        fun remove(pid: PID) {
            if (!map.containsKey(pid)) {
                throw Error("Player doesn't exist")
            }

            val removedPlayer = map.remove(pid)
            removedPlayer!!.hooks.onRemovedFromMap(this@GameMap)
        }

        fun get(pid: PID): Player {
            return map[pid] ?: throw Error("Player not found")
        }

        fun getAll(): Collection<Player> {
            return map.values
        }

        fun moveTo(pid: PID, targetPosition: WorldPosition) {
            val player = get(pid)
            player.startMovingTo(targetPosition)
        }
    }

    fun onPhysicsStep(deltaTime: Float) {
        val velocityIterations = 6
        val positionIterations = 2
        physics.step(deltaTime, velocityIterations, positionIterations)

        players.getAll().forEach {
            it.afterPhysicsUpdate(deltaTime)
        }
    }

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
}
