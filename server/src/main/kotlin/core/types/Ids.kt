package core.types

import com.badlogic.gdx.math.Vector2

inline class ItemInstanceId(val value: UInt) {
    companion object {
        private var counter = 0u
        fun unique() = ItemInstanceId(++counter)
    }
}

inline class ItemSchemaId(val value: UShort)

inline class IID(val value: UInt) {
    companion object {
        private var counter = 0u
        fun unique() = ItemInstanceId(++counter)
    }
}

inline class GameMapId(val value: UInt)
inline class SpriteId(val value: UShort)
inline class Coordinate(val value: Int)
inline class CreatureName(val value: String) {
    init {
        require(value.length <= 30)
    }
}

// Used very often as an Int, but in realty should be Ubyte
inline class TileRadius(val value: Int) {
    init {
        require(value >= 0)
    }
}

// Used very often as an Int, but in realty should be UShort
inline class WorldRadius(val value: Int) {
    init {
        require(value >= 0)
    }
}
inline class ResourceId(val value: UShort)

typealias WorldPosition = Vector2

data class GridPosition(
    val x: Coordinate,
    val y: Coordinate
)
