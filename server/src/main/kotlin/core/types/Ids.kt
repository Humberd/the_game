package core.types

import com.badlogic.gdx.math.Vector2

// Unique PlayerId
inline class PID(val value: UInt) {
    companion object {
        private var counter = 0u;
        fun unique() = PID(++counter)
    }
}
// Unique CreatureId
inline class CID(val value: UInt) {
    companion object {
        private var counter = 0u
        fun unique() = CID(++counter)
    }
}
inline class ItemInstanceId(val value: UInt) {
    companion object {
        private var counter = 0u
        fun unique() = ItemInstanceId(++counter)
    }
}

inline class IID(val value: UInt) {
    companion object {
        private var counter = 0u
        fun unique() = ItemInstanceId(++counter)
    }
}

//Unique SpellId
inline class SID(val value: UInt) {
    companion object {
        private var counter = 0u
        fun unique() = SID(++counter)
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
inline class Experience(val value: Long) {
    init {
        require(value > 0)
    }
    fun toLevel(): Level {
        return Level((value / 100f).toInt() + 1  )
    }
}
inline class Level(val value: Int) {
    init {
        require(value > 0)
    }
}

typealias WorldPosition = Vector2

data class GridPosition(
    val x: Coordinate,
    val y: Coordinate
)
