package core.maps

private const val GRAVEL_SPRITE: UShort = 0u
private const val GRASS_SPRITE: UShort = 1u

object GameMapGenerator {
    fun generateMap1(width: Int, height: Int): GameMap {
        val grid = ArrayList<GameMap.Tile>(width * height)

        for (x in 0..width) {
            for (y in 0..height) {
                grid.add(
                    GameMap.Tile(
                        spriteId = if (x % 4 == 0) GRAVEL_SPRITE else GRASS_SPRITE,
                        position = GameMap.GridPosition(x, y)
                    )
                )
            }
        }

        return GameMap(
            id = GameMapId(1u),
            grid = grid
        )
    }
}
