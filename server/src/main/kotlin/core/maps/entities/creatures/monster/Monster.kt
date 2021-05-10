package core.maps.entities.creatures.monster

import core.StateChangeNotifier
import core.maps.entities.CollisionCategory
import core.maps.entities.GameMap
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.CreatureSeed
import mu.KLogging

class Monster(
    creatureSeed: CreatureSeed,
    gameMap: GameMap,
    notifier: StateChangeNotifier,
    monsterSeed: MonsterSeed
) : Creature(creatureSeed, gameMap, notifier) {
    companion object : KLogging()

    val detectionRadius = monsterSeed.detectionRadius
    val chaseRadius = monsterSeed.chaseRadius

    val aiMovement = MonsterAiMovement(this)

    override val hooks = MonsterHooks()
    override val collisionCategory = CollisionCategory.MONSTER

    override fun onInit() {
        super.onInit()
        aiMovement.onInit()
    }

    override fun afterPhysicsUpdate(deltaTime: Float) {
        super.afterPhysicsUpdate(deltaTime)

        aiMovement.afterPhysicsUpdate(deltaTime)
    }

    override fun toString() = "Monster($cid)"
}
