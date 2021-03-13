package utils

import core.types.Coordinate
import core.types.GridPosition
import core.types.WorldPosition
import kotlin.math.floor

fun getDistance(pos1: WorldPosition, pos2: WorldPosition): Double {
    val ac = Math.abs(pos1.x - pos2.x)
    val cb = Math.abs(pos1.y - pos2.y)

    return Math.hypot(ac.toDouble(), cb.toDouble())
}

fun toGridPosition(position: WorldPosition): GridPosition {
    return GridPosition(
        x = Coordinate(floor(position.x).toInt()),
        y = Coordinate(floor(position.y).toInt())
    )
}
