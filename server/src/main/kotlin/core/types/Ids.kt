package core.types

import org.mini2Dx.gdx.math.Vector2

inline class PID(val value: UInt)
inline class GameMapId(val value: UInt)
inline class SpriteId(val value: UShort)
inline class Coordinate(val value: Int)
inline class ItemId(val value: UShort)
inline class InstanceId(val value: UInt)
inline class PlayerName(val value: String)
inline class CollisionRadius(val value: UShort)

typealias WorldPosition = Vector2
