package core.maps.entities

import core.StateChangeNotifier
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.CreatureSeed

data class MonsterSeed(
    val attackTriggerRadius: Float
)

class Monster(
    creatureSeed: CreatureSeed,
    gameMap: GameMap,
    notifier: StateChangeNotifier,
    monsterSeed: MonsterSeed
) : Creature(creatureSeed, gameMap, notifier) {
    val attackTriggerRadius = monsterSeed.attackTriggerRadius

    override val hooks = MonsterHooks()
    override fun collisionCategory() = CollisionCategory.MONSTER

}
