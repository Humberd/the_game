package core.maps.entities

import kotlin.experimental.or

enum class CollisionCategory(val value: Short) {
    PLAYER(0x0001) {
        override fun collidesWith() = 0.toShort()
    },
    MONSTER(0x0002) {
        override fun collidesWith() = TERRAIN.value
    },
    TERRAIN(0x0004) {
        override fun collidesWith() = MONSTER.value
    };

    abstract fun collidesWith(): Short

    infix fun or(other: CollisionCategory): Short = this.value or other.value
    infix fun Short.or(other: CollisionCategory): Short = this or other.value
}
