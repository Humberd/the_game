package core.maps.entities.creatures.monster

import core.maps.entities.creatures.CreatureSeed

data class MonsterSeed(
    val detectionRadius: Float,
    val chaseRadius: Float,
    val creatureSeed: CreatureSeed
)
