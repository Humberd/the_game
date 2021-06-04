package core.maps.entities.creatures.monster

import core.maps.entities.CollisionCategory
import core.maps.entities.GameContext
import core.maps.entities.creatures.Creature
import mu.KLogging

class Monster(
    monsterSeed: MonsterSeed,
    context: GameContext
) : Creature(monsterSeed.creatureSeed, context) {
    companion object : KLogging()

    val detectionRadius = monsterSeed.detectionRadius
    val chaseRadius = monsterSeed.chaseRadius

    val aiMovement = MonsterAiMovement(this)

    override val hooks = MonsterHooks()
    override val collisionCategory = CollisionCategory.MONSTER

    override fun onInit() {
        super.onInit()
        logger.info { "Invoked" }
        aiMovement.onInit()
    }

    override fun onUpdate(deltaTime: Float) {
        super.onUpdate(deltaTime)

        aiMovement.afterPhysicsUpdate(deltaTime)
    }

    override fun toString() = "Monster($cid)"
}
