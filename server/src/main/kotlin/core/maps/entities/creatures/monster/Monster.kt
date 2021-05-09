package core.maps.entities.creatures.monster

import core.StateChangeNotifier
import core.maps.entities.CollisionCategory
import core.maps.entities.GameMap
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.CreatureSeed

data class MonsterSeed(
    val detectionRadius: Float,
    val chaseRadius: Float
)

class Monster(
    creatureSeed: CreatureSeed,
    gameMap: GameMap,
    notifier: StateChangeNotifier,
    monsterSeed: MonsterSeed
) : Creature(creatureSeed, gameMap, notifier) {
    val detectionRadius = monsterSeed.detectionRadius
    val chaseRadius = monsterSeed.chaseRadius

    override val hooks = MonsterHooks()
    override val collisionCategory = CollisionCategory.MONSTER

}
