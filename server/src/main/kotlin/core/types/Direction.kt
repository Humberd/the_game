package core.types

import org.mini2Dx.gdx.math.Vector2

data class Direction(
    val isUp: Boolean,
    val isRight: Boolean,
    val isDown: Boolean,
    val isLeft: Boolean,
) {
    companion object {
        const val UP: UByte = 0x01u
        const val RIGHT: UByte = 0x02u
        const val DOWN: UByte = 0x04u
        const val LEFT: UByte = 0x08u
    }

    fun toVector2(): Vector2 {
        val directionVector = Vector2()

        if (isUp) directionVector.y += -1
        if (isDown) directionVector.y += 1
        if (isRight) directionVector.x += 1
        if (isLeft) directionVector.x += -1

        return directionVector
    }
}

inline class DirectionByte(val byte: UByte) {
    fun toDirection(): Direction {
        return Direction(
            isUp = byte and Direction.UP > 0u,
            isRight = byte and Direction.RIGHT > 0u,
            isDown = byte and Direction.DOWN > 0u,
            isLeft = byte and Direction.LEFT > 0u,

            )
    }
}
