package core.maps.entities.creatures.player

import core.PlayerNotifier
import core.maps.entities.CollisionCategory
import core.maps.entities.GameContext
import core.maps.entities.creatures.Creature

class Player(
    playerSeed: PlayerSeed,
    context: GameContext,
    notifier: PlayerNotifier
) : Creature(playerSeed.creatureSeed, context) {
    val pid = playerSeed.pid

    override val hooks = PlayerHooks(this, notifier)
    override val collisionCategory = CollisionCategory.PLAYER

    override fun toString(): String {
        return "Player($pid, $cid)"
    }
}
