package core.maps.entities.creatures.player

import core.StateChangeNotifier
import core.maps.entities.GameMap
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.CreatureHooks
import core.maps.entities.items.Item
import core.maps.shapes.Wall
import infrastructure.udp.models.convert
import mu.KotlinLogging
import pl.humberd.udp.packets.serverclient.DamageTaken

private val logger = KotlinLogging.logger {}


class PlayerHooks(
    private val player: Player,
    private val notifier: StateChangeNotifier
) : CreatureHooks {
    override fun onAddedToMap(gameMap: GameMap) {
        notifier.notifyPlayerDetails(player.pid, player)
        notifier.notifyCreatureUpdate(player, player)
        notifier.notifyTerrainUpdate(player)

        // Make aware other creatures that can see me
        gameMap.creatures.getAllCreatures()
            .filter { it.cid != player.cid }
            .filter { it.canSee(player) }
            .forEach {
                it.cache.creaturesISee.register(player)
            }

        // Tell me about other creatures that I can see
        player.getGreedyVisibleCreatures()
            .forEach {
                player.cache.creaturesISee.register(it)
            }
    }

    override fun onRemovedFromMap(gameMap: GameMap) {
        if (player.combat.isCurrentlyAttacking()) {
            player.combat.stopAttacking()
        }

        notifier.notifyCreatureDisappear(player.pid, player)

        player.cache.creaturesThatSeeMe.toTypedArray().forEach {
            it.cache.creaturesISee.unregister(player)
        }

        player.cache.creaturesISee.getAll().forEach {
            player.cache.creaturesISee.unregister(it)
        }
    }

    override fun onMoved(tileChanged: Boolean) {
        notifier.notifyCreaturePositionUpdate(player.pid, player)
        if (tileChanged) {
            notifier.notifyTerrainUpdate(player)
        }
    }

    override fun onCollideWith(wall: Wall) {
        logger.debug { "Colliding with Wall" }
        player.movement.stopMoving()
    }

    override fun onOtherCreatureAppearInViewRange(otherCreature: Creature) {
        notifier.notifyCreatureUpdate(player, otherCreature)
    }

    override fun onOtherCreatureDisappearFromViewRange(otherCreature: Creature) {
        if (player.combat.attackedTarget === otherCreature) {
            player.combat.stopAttacking()
        }
        notifier.notifyCreatureDisappear(player.pid, otherCreature)
    }

    override fun onOtherCreaturePositionChange(otherCreature: Creature) {
        notifier.notifyCreaturePositionUpdate(player.pid, otherCreature)
    }

    override fun onSelfDamageTaken(damage: UInt) {
        notifier.notifyDamageTaken(
            player.pid, arrayOf(
                DamageTaken.Damage(player.position.convert(), damage)
            )
        )
        notifier.notifyCreatureUpdate(player, player)
    }

    override fun onOtherCreatureDamageTaken(otherCreature: Creature, damage: UInt) {
        notifier.notifyDamageTaken(
            player.pid, arrayOf(
                DamageTaken.Damage(otherCreature.position.convert(), damage)
            )
        )
        notifier.notifyCreatureUpdate(player, otherCreature)
    }

    override fun onStartAttackOtherCreature(otherCreature: Creature) {
        notifier.notifyCreatureUpdate(player, otherCreature)
    }

    override fun onStoppedAttackOtherCreature(otherCreature: Creature) {
        notifier.notifyCreatureUpdate(player, otherCreature)
    }

    override fun onItemEquipped(item: Item) {
        notifier.notifyPlayerStats(player)
    }
}
