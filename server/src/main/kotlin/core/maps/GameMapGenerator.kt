package core.maps

import core.maps.entities.GameMap
import core.maps.entities.Item
import core.maps.entities.Monster
import core.maps.entities.Tile
import core.types.*
import gameland.items.TeleportActionHandler

private const val GRAVEL_SPRITE: UShort = 0u
private const val GRASS_SPRITE: UShort = 1u

object GameMapGenerator {
    fun generateMap1(width: Int, height: Int): GameMap {
        val grid = Array(width) { x ->
            Array(height) { y ->
                Tile(
                    spriteId = SpriteId(if (x % 4 == 0) GRAVEL_SPRITE else GRASS_SPRITE),
                    gridPosition = GameMap.GridPosition(Coordinate(x), Coordinate(y))
                )
            }
        }

        return GameMap(
            id = GameMapId(1u),
            gridWidth = width,
            gridHeight = height,
            grid = grid,
            items = listOf(
                Item(
                    iid = IID.unique(),
                    itemDef = ItemDefinitionStore.get(ItemType.JUST_A_KNIFE),
                    position = WorldPosition(200f, 300f)
                ),
                Item(
                    iid = IID.unique(),
                    itemDef = ItemDefinitionStore.get(ItemType.WOODEN_SHIELD),
                    position = WorldPosition(156f, 436f)
                ),
                Item(
                    iid = IID.unique(),
                    itemDef = ItemDefinitionStore.get(ItemType.GOLD_BAR),
                    position = WorldPosition(400f, 250f)
                ),
                Item(
                    iid = IID.unique(),
                    itemDef = ItemDefinitionStore.get(ItemType.GOLD_BAR),
                    position = WorldPosition(400f, 270f)
                ),
                Item(
                    iid = IID.unique(),
                    itemDef = ItemDefinitionStore.get(ItemType.GOLD_BAR),
                    position = WorldPosition(400f, 290f)
                ),
                Item(
                    iid = IID.unique(),
                    itemDef = ItemDefinitionStore.get(ItemType.TELEPORT),
                    position = WorldPosition(500f, 580f),
                    actionHandler = TeleportActionHandler
                )
            ),
            creatures = listOf(
                Monster(
                    cid = CID.unique(),
                    name = CreatureName("Ghost"),
                    health = 100u,
                    spriteId = SpriteId(6u),
                    position = WorldPosition(300f, 300f)
                )
            )
        )
    }
}
