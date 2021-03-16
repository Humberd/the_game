package core.maps.entities

import core.StateChangeNotifier
import core.maps.shapes.Wall
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class PlayerHooks(
    private val player: Player,
    private val notifier: StateChangeNotifier
) : CreatureHooks {
    override fun onAddedToMap(gameMap: GameMap) {
        notifier.notifyPlayerDetails(player.pid, player)
        notifier.notifyCreatureUpdate(player.pid, player)
        notifier.notifyTerrainUpdate(player)

        // Make aware other creatures that can see me
        gameMap.creatures.getAllCreatures()
            .filter { it.cid != player.cid }
            .filter { it.canSee(player) }
            .forEach {
                it.creaturesISee.register(player)
            }

        // Tell me about other creatures that I can see
        player.getGreedyVisibleCreatures()
            .forEach {
                player.creaturesISee.register(it)
            }
    }

    override fun onRemovedFromMap(gameMap: GameMap) {
        notifier.notifyCreatureDisappear(player.pid, player)

        player.creaturesThatSeeMe.forEach {
            it.creaturesISee.unregister(player)
        }

        player.creaturesISee.getAll().forEach {
            player.creaturesISee.unregister(it)
        }
    }

    override fun onMoved() {
        notifier.notifyCreatureUpdate(player.pid, player)
        notifier.notifyTerrainUpdate(player)
    }

    override fun onCollideWith(wall: Wall) {
        logger.debug { "Colliding with Wall" }
        player.stopMoving()
    }

    override fun onOtherCreatureAppearInViewRange(otherCreature: Creature) {
        notifier.notifyCreatureUpdate(player.pid, otherCreature)
    }

    override fun onOtherCreatureDisappearFromViewRange(otherCreature: Creature) {
        notifier.notifyCreatureDisappear(player.pid, otherCreature)
    }

    override fun onOtherCreaturePositionChange(otherCreature: Creature) {
        notifier.notifyCreaturePositionUpdate(player.pid, otherCreature)
    }
}
