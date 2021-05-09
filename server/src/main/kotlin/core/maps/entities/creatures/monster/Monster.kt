package core.maps.entities.creatures.monster

import core.StateChangeNotifier
import core.maps.entities.CollisionCategory
import core.maps.entities.GameMap
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.CreatureSeed
import ktx.box2d.circle

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

    override fun onInit() {
        super.onInit()
        physics.body.circle(radius = detectionRadius) {
            density = 0f
            friction = 0f
            restitution = 0f
            userData = this@Monster
            isSensor = true
            filter.categoryBits = CollisionCategory.DETECTION.value
            filter.maskBits = CollisionCategory.DETECTION.collidesWith()
        }

        physics.body.circle(radius = chaseRadius) {
            density = 0f
            friction = 0f
            restitution = 0f
            userData = this@Monster
            isSensor = true
            filter.categoryBits = CollisionCategory.DETECTION.value
            filter.maskBits = CollisionCategory.DETECTION.collidesWith()
        }
    }
}
