package core.maps.entities

import com.badlogic.gdx.math.Vector2
import core.maps.entities.creatures.Creature
import core.maps.shapes.Wall
import core.types.GameMapId
import core.types.GridPosition
import ktx.box2d.body
import ktx.box2d.createWorld
import ktx.box2d.loop
import org.recast4j.recast.geom.InputGeomProvider
import pl.humberd.models.CID
import pl.humberd.models.PID
import utils.toGridPosition

class GameMap(
    val id: GameMapId,
    val gridWidth: Int,
    val gridHeight: Int,
    private val grid: Array<Array<Tile>>,
    navigationProvider: InputGeomProvider
) : GameContext() {
    val navigation = GameMapNavigation()

    //region Physics Initialization
    override val physics = createWorld(gravity = Vector2(0f, 0f), allowSleep = false)

    init {
        physics.setContactListener(GameMapContactListener())
        grid.forEach { it.forEach { it.onInit(physics) } }
        initMapBounds()
        navigation.onInit(navigationProvider)
        GameMapDebugRenderer(this)
    }

    private fun initMapBounds() {
        val vertices = arrayOf(
            Vector2(0f, 0f),
            Vector2(gridWidth.toFloat(), 0f),
            Vector2(gridWidth.toFloat(), gridHeight.toFloat()),
            Vector2(0f, gridHeight.toFloat())
        )

        physics.body {
            val wall = Wall()
            userData = wall
            loop(*vertices) {
                userData = wall
                density = 0f
                friction = 0f
                restitution = 0f
                filter.categoryBits = CollisionCategory.TERRAIN.value
                filter.maskBits = CollisionCategory.TERRAIN.collidesWith()
            }
        }
    }

    //endregion

    fun onPhysicsStep(deltaTime: Float) {
        val velocityIterations = 6
        val positionIterations = 2
        physics.step(deltaTime, velocityIterations, positionIterations)

        projectiles.forEach { it.onUpdate(deltaTime) }
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

    fun getTileFor(gameMapObject: GameMapObject): Tile {
        return getTileAt(toGridPosition(gameMapObject.position))
    }
    //endregion
}
