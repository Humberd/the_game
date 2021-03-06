package core.types

import org.mini2Dx.gdx.math.Vector2

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
// Unique ItemId
inline class IID(val value: UInt) {
    companion object {
        private var counter = 0u
        fun unique() = IID(++counter)
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
inline class CollisionRadius(val value: UShort)

typealias WorldPosition = Vector2
