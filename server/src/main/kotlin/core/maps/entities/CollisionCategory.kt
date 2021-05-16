package core.maps.entities

import kotlin.experimental.or

enum class CollisionCategory(val value: Short) {
    NOTHING(0x0000) {
        override fun collidesWith() = NOTHING.value
    },
    PLAYER(0x0001) {
        override fun collidesWith() = DETECTION or PROJECTILE
    },
    MONSTER(0x0002) {
        override fun collidesWith() = DETECTION or PROJECTILE
    },
    TERRAIN(0x0004) {
        override fun collidesWith() = NOTHING.value
    },
    DETECTION(0x0008) {
        override fun collidesWith() = PLAYER or MONSTER or PROJECTILE
    },
    PROJECTILE(0x0010) {
        override fun collidesWith() = PLAYER or MONSTER or DETECTION
    };

    abstract fun collidesWith(): Short

    private infix fun or(other: CollisionCategory): Short = this.value or other.value
    infix fun Short.or(other: CollisionCategory): Short = this or other.value
}
