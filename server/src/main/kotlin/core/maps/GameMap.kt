package core.maps

inline class GameMapId(val value: UInt)

class GameMap(
    val id: GameMapId,
    private val grid: List<Tile>
) {
    class Tile(
        val spriteId: UShort,
        val position: GridPosition
    )

    class GridPosition(
        val x: Int,
        val y: Int
    )
}
