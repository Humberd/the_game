package core.maps.entities

import core.StateChangeNotifier
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.CreatureHooks
import core.maps.shapes.Wall
import infrastructure.udp.egress.EgressDataPacket
import mu.KotlinLogging

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
                it.creaturesISee.register(player)
            }

        // Tell me about other creatures that I can see
        player.getGreedyVisibleCreatures()
            .forEach {
                player.creaturesISee.register(it)
            }
    }

    override fun onRemovedFromMap(gameMap: GameMap) {
        if (player.combat.isCurrentlyAttacking()) {
            player.combat.stopAttacking()
        }

        notifier.notifyCreatureDisappear(player.pid, player)

        player.creaturesThatSeeMe.toTypedArray().forEach {
            it.creaturesISee.unregister(player)
        }

        player.creaturesISee.getAll().forEach {
            player.creaturesISee.unregister(it)
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
        player.stopMoving()
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
                EgressDataPacket.DamageTaken.Damage(player.position, damage)
            )
        )
        notifier.notifyCreatureUpdate(player, player)
    }

    override fun onOtherCreatureDamageTaken(otherCreature: Creature, damage: UInt) {
        notifier.notifyDamageTaken(
            player.pid, arrayOf(
                EgressDataPacket.DamageTaken.Damage(otherCreature.position, damage)
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
}
