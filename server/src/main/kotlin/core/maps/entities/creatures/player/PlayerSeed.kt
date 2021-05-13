package core.maps.entities.creatures.player

import core.maps.entities.creatures.CreatureSeed
import pl.humberd.models.PID

data class PlayerSeed(
    val pid: PID,
    val spellsContainer: SpellsContainer,
    val creatureSeed: CreatureSeed
)
