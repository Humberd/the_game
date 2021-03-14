package core.maps.entities

import core.StateChangeNotifier

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


    override fun onOtherCreatureDisappearFromViewRange(otherCreature: Creature) {
    }

    override fun onOtherCreatureAppearInViewRange(otherCreature: Creature) {
    }

    override fun onOtherCreaturePositionChange(otherCreature: Creature) {
    }

}
