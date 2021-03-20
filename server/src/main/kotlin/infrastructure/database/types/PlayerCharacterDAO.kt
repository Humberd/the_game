package infrastructure.database.types

import core.maps.entities.PlayerSeed
import core.maps.entities.SpellsContainer
import core.maps.entities.creatures.CreatureSeed
import core.types.*

data class PlayerCharacterDAO(
    val pid: PID,
    val name: CreatureName,
    val experience: Experience,
    val spriteId: SpriteId,
    val position: WorldPosition,
    val tilesViewRadius: TileRadius,
    val bodyRadius: Float
) {
    fun toCreatureSeed() = CreatureSeed(
        name,
        experience,
        spriteId,
        position,
        tilesViewRadius,
        bodyRadius
    )

    fun toPlayerSeed() = PlayerSeed(
        pid = pid,
        spellsContainer = SpellsContainer()
    )
}
