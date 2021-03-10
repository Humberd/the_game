package utils

import core.types.WorldPosition

fun getDistance(pos1: WorldPosition, pos2: WorldPosition): Double {
    val ac = Math.abs(pos1.x - pos2.x)
    val cb = Math.abs(pos1.y - pos2.y)

    return Math.hypot(ac.toDouble(), cb.toDouble())
}
