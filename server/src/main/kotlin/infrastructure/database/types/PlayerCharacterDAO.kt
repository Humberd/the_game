package infrastructure.database.types

import core.maps.entities.CreatureSeed
import core.maps.entities.PlayerSeed
import core.maps.entities.SpellsContainer
import core.types.*

data class PlayerCharacterDAO(
    val pid: PID,
    val name: CreatureName,
    val baseHealth: UInt,
    val currentHealth: UInt,
    val spriteId: SpriteId,
    val position: WorldPosition,
    val velocity: Float,
    val tilesViewRadius: TileRadius,
    val bodyRadius: Float
) {
    fun toCreatureSeed() = CreatureSeed(
        name,
        baseHealth,
        currentHealth,
        spriteId,
        position,
        velocity,
        tilesViewRadius,
        bodyRadius
    )

    fun toPlayerSeed() = PlayerSeed(
        pid = pid,
        spellsContainer = SpellsContainer()
    )
}
