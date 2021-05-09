package core.maps.entities.creatures.player

import pl.humberd.models.PID

data class PlayerSeed(
    val pid: PID,
    val spellsContainer: SpellsContainer
)
