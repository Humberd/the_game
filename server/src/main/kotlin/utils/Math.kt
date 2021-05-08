package utils

import com.badlogic.gdx.math.Vector2
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


/**
 * @see https://stackoverflow.com/a/34831275/4256929
 */
fun Vector2.isOnLineBetween(start: Vector2, end: Vector2): Boolean {
    val diff = Math.abs(((y - start.y) / (x - start.x)) - ((y - end.y) / (x - end.x)))
    println(diff)
    return diff < 0.00001f
}
