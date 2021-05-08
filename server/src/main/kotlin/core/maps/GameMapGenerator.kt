package core.maps

import core.maps.entities.GameMap
import core.maps.entities.Tile
import core.maps.obstacles.Obstacle
import core.types.Coordinate
import core.types.GameMapId
import core.types.GridPosition
import core.types.SpriteId
import de.lighti.clipper.Path
import de.lighti.clipper.Paths
import io.map.ObjImporter
import io.map.PolygonUtils

private const val GRAVEL_SPRITE: UShort = 16u
private const val GRASS_SPRITE: UShort = 17u

object GameMapGenerator {
//    fun generateMap1(width: Int, height: Int, notifier: StateChangeNotifier): GameMap {
//        val grid = Array(width) { x ->
//            Array(height) { y ->
//                Tile(
//                    spriteId = SpriteId(if (x % 4 == 0) GRAVEL_SPRITE else GRASS_SPRITE),
//                    gridPosition = GridPosition(Coordinate(x), Coordinate(y))
//                )
//            }
//        }
//
//        val gameMap = GameMap(
//            id = GameMapId(1u),
//            gridWidth = width,
//            gridHeight = height,
//            grid = grid
//        )
//
//        val monsters = listOf(
//            Monster(
//                creatureSeed = CreatureSeed(
//                    name = CreatureName("Ghost"),
//                    experience = Experience(1074L),
//                    spriteId = SpriteId(6u),
//                    position = WorldPosition(6f, 2f),
//                    tilesViewRadius = TileRadius(3),
//                    bodyRadius = 0.5f,
//                    equipment = emptyMap(),
//                    backpack = emptyArray()
//                ),
//                gameMap = gameMap,
//                notifier = notifier,
//                monsterSeed = MonsterSeed(
//                    attackTriggerRadius = 0.1f
//                )
//            )
//        )
//
//        monsters.forEach {
//            gameMap.creatures.add(it)
//        }
//
//        return gameMap
//    }

    fun generateObjMap(): GameMap {
        val objData =
            ObjImporter.load(ObjImporter::class.java.getResourceAsStream("/assets/blender/example-plane.obj")!!)
        val obstacles = objData.obstacles.map { PolygonUtils.convert3dPolygonTo2d(it) }
        val extrapolatedObstacles =
            obstacles.map { obstacle -> Path().also { it.addAll(PolygonUtils.convertFloatPolygonToLong(obstacle)) } }
        val obstaclesPaths = Paths().also { it.addAll(extrapolatedObstacles) }

        val grid = Array(20) { x ->
            Array(20) { y ->
                Tile(
                    spriteId = SpriteId(if (x % 4 == 0) GRAVEL_SPRITE else GRASS_SPRITE),
                    gridPosition = GridPosition(Coordinate(x), Coordinate(y)),
                    obstacles = PolygonUtils.findPathsFor(x, y, obstaclesPaths).map { Obstacle(it) }
                )
            }
        }

        return GameMap(
            GameMapId(1u),
            20,
            20,
            grid,
            objData.provider
        )
    }
}
