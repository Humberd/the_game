package core.maps

import core.StateChangeNotifier
import core.maps.entities.GameMap
import core.maps.entities.Tile
import core.maps.entities.creatures.CreatureSeed
import core.maps.entities.creatures.monster.Monster
import core.maps.entities.creatures.monster.MonsterSeed
import core.types.*

private const val GRAVEL_SPRITE: UShort = 0u
private const val GRASS_SPRITE: UShort = 1u

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

        val gameMap =  GameMap(
            id = GameMapId(1u),
            gridWidth = width,
            gridHeight = height,
            grid = grid,
            gameMapObjects = listOf(
//                Item(
//                    iid = IID.unique(),
//                    itemDef = ItemDefinitionStore.get(ItemType.JUST_A_KNIFE),
//                    position = WorldPosition(200f, 300f)
//                ),
//                Item(
//                    iid = IID.unique(),
//                    itemDef = ItemDefinitionStore.get(ItemType.WOODEN_SHIELD),
//                    position = WorldPosition(156f, 436f)
//                ),
//                Item(
//                    iid = IID.unique(),
//                    itemDef = ItemDefinitionStore.get(ItemType.GOLD_BAR),
//                    position = WorldPosition(400f, 250f)
//                ),
//                Item(
//                    iid = IID.unique(),
//                    itemDef = ItemDefinitionStore.get(ItemType.GOLD_BAR),
//                    position = WorldPosition(400f, 270f)
//                ),
//                Item(
//                    iid = IID.unique(),
//                    itemDef = ItemDefinitionStore.get(ItemType.GOLD_BAR),
//                    position = WorldPosition(400f, 290f)
//                ),
//                Item(
//                    iid = IID.unique(),
//                    itemDef = ItemDefinitionStore.get(ItemType.TELEPORT),
//                    position = WorldPosition(500f, 580f),
//                    actionHandler = TeleportActionHandler
//                )
            ),
        )

        val monsters = listOf(
            Monster(
                creatureSeed = CreatureSeed(
                    name = CreatureName("Ghost"),
                    experience = Experience(1000L),
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

        return gameMap
    }
}
