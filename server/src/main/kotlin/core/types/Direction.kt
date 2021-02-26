package core.types

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
