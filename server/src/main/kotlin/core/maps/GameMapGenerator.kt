package core.maps

import com.badlogic.gdx.math.Vector2
import core.StateChangeNotifier
import core.maps.entities.GameMap
import core.maps.entities.Tile
import core.maps.entities.creatures.CreatureSeed
import core.maps.entities.creatures.monster.Monster
import core.maps.entities.creatures.monster.MonsterSeed
import core.types.*
import de.lighti.clipper.Clipper
import de.lighti.clipper.DefaultClipper
import de.lighti.clipper.Path
import de.lighti.clipper.Paths
import io.map.ObjImporter
import io.map.PolygonUtils
import pl.humberd.models.Experience

private const val GRAVEL_SPRITE: UShort = 16u
private const val GRASS_SPRITE: UShort = 17u

object GameMapGenerator {
    fun generateMap1(width: Int, height: Int, notifier: StateChangeNotifier): GameMap {
        val grid = Array(width) { x ->
            Array(height) { y ->
                Tile(
                    spriteId = SpriteId(if (x % 4 == 0) GRAVEL_SPRITE else GRASS_SPRITE),
                    gridPosition = GridPosition(Coordinate(x), Coordinate(y))
                )
            }
        }

        val gameMap = GameMap(
            id = GameMapId(1u),
            gridWidth = width,
            gridHeight = height,
            grid = grid
        )

        val monsters = listOf(
            Monster(
                creatureSeed = CreatureSeed(
                    name = CreatureName("Ghost"),
                    experience = Experience(1074L),
                    spriteId = SpriteId(6u),
                    position = WorldPosition(6f, 2f),
                    tilesViewRadius = TileRadius(3),
                    bodyRadius = 0.5f,
                    equipment = emptyMap(),
                    backpack = emptyArray()
                ),
                gameMap = gameMap,
                notifier = notifier,
                monsterSeed = MonsterSeed(
                    attackTriggerRadius = 0.1f
                )
            )
        )

        monsters.forEach {
            gameMap.creatures.add(it)
        }

        gameMap.createWallsPolygon(
            WorldPosition(2f, 4f),
            WorldPosition(3f, 4.5f),
            WorldPosition(3f, 5.2f),
            WorldPosition(2f, 5f),
            WorldPosition(1.5f, 4.5f),
        )

        return gameMap
    }

    fun generateObjMap() {
        val objData =
            ObjImporter.load(ObjImporter::class.java.getResourceAsStream("/assets/blender/example-plane.obj")!!)
        val obstacles = objData.obstacles.map { PolygonUtils.convert3dPolygonTo2d(it) }
        val extrapolatedObstacles =
            obstacles.map { obstacle -> Path().also { it.addAll(PolygonUtils.convertFloatPolygonToLong(obstacle)) } }
        val obstaclesPaths = Paths().also { it.addAll(extrapolatedObstacles) }

        val grid = Array(20) { x ->
            Array(20) { y ->
                val solution = Paths()
                val clipper = DefaultClipper(Clipper.STRICTLY_SIMPLE)
                clipper.addPaths(obstaclesPaths, Clipper.PolyType.SUBJECT, true)
                val bound = arrayOf(
                    Vector2(x.toFloat(), y.toFloat()),
                    Vector2(x.toFloat(), y.toFloat() + 1),
                    Vector2(x.toFloat() + 1, y.toFloat() + 1),
                    Vector2(x.toFloat() + 1, y.toFloat()),
                    Vector2(x.toFloat(), y.toFloat())
                )
                clipper.addPath(
                    Path().also { it.addAll(PolygonUtils.convertFloatPolygonToLong(bound)) },
                    Clipper.PolyType.CLIP,
                    true
                )

                val hasResults = clipper.execute(Clipper.ClipType.INTERSECTION, solution)
                if (hasResults) {
                    println(solution)
                }

                Tile(
                    spriteId = SpriteId(if (x % 4 == 0) GRAVEL_SPRITE else GRASS_SPRITE),
                    gridPosition = GridPosition(Coordinate(x), Coordinate(y))
                )
            }
        }
    }
}
