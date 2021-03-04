package core.maps

import core.types.*
import org.mini2Dx.gdx.math.Vector2

private const val GRAVEL_SPRITE: UShort = 0u
private const val GRASS_SPRITE: UShort = 1u

object GameMapGenerator {
    fun generateMap1(width: Int, height: Int): GameMap {
        val grid = Array(width) { x ->
            Array(height) { y ->
                GameMap.Tile(
                    spriteId = SpriteId(if (x % 4 == 0) GRAVEL_SPRITE else GRASS_SPRITE),
                    gridPosition = GameMap.GridPosition(Coordinate(x), Coordinate(y))
                )
            }
        }

        return GameMap(
            id = GameMapId(1u),
            gridWidth = width,
            gridHeight = height,
            worldOffset = Vector2.Zero,
            grid = grid,
            listOf(
                GameMap.Item(
                    instanceId = InstanceId(1u),
                    itemDef = ItemDefinitionStore.get(ItemId(1u)),
                    position = WorldPosition(200f, 300f)
                ),
                GameMap.Item(
                    instanceId = InstanceId(2u),
                    itemDef = ItemDefinitionStore.get(ItemId(2u)),
                    position = WorldPosition(156f, 436f)
                ),
                GameMap.Item(
                    instanceId = InstanceId(3u),
                    itemDef = ItemDefinitionStore.get(ItemId(3u)),
                    position = WorldPosition(400f, 250f)
                ),
                GameMap.Item(
                    instanceId = InstanceId(4u),
                    itemDef = ItemDefinitionStore.get(ItemId(3u)),
                    position = WorldPosition(400f, 270f)
                ),
                GameMap.Item(
                    instanceId = InstanceId(5u),
                    itemDef = ItemDefinitionStore.get(ItemId(3u)),
                    position = WorldPosition(400f, 290f)
                ),
            )
        )
    }
}
