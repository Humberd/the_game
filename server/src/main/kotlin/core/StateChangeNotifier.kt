package core

import core.maps.entities.Monster
import core.maps.entities.Player
import core.maps.entities.creatures.Creature
import core.types.PID
import infrastructure.udp.egress.EgressDataPacket
import infrastructure.udp.egress.UdpEgressPacketHandler

class StateChangeNotifier(
    private val egressPacketHandler: UdpEgressPacketHandler
) {
    fun notifyCreatureUpdate(to: Player, creature: Creature) {
        egressPacketHandler.notify(
            to.pid,
            EgressDataPacket.CreatureUpdate(
                cid = creature.cid,
                name = creature.name,
                baseHealth = creature.stats.healthPool.current,
                currentHealth = creature.stats.healthCurrent,
                position = creature.position,
                spriteId = creature.spriteId,
                bodyRadius = creature.bodyRadius,
                attackTriggerRadius = if (creature is Monster) creature.attackTriggerRadius else 0f,
                isBeingAttackedByMe = to.combat.attackedTarget === creature
            )
        )
    }

    fun notifyCreatureDisappear(to: PID, creature: Creature) {
        egressPacketHandler.notify(
            to,
            EgressDataPacket.CreatureDisappear(creature.cid)
        )
    }

    fun notifyCreaturePositionUpdate(to: PID, creature: Creature) {
        egressPacketHandler.notify(
            to,
            EgressDataPacket.CreaturePositionUpdate(
                creature.cid,
                position = creature.position
            )
        )
    }

    fun notifyPlayerDetails(to: PID, player: Player) {
        egressPacketHandler.notify(
            to,
            EgressDataPacket.PlayerDetails(
                pid = player.pid,
                cid = player.cid
            )
        )
    }

    fun notifyTerrainUpdate(player: Player) {
        val tiles = player.lastUpdate.tileSlice

        egressPacketHandler.notify(
            player.pid,
            EgressDataPacket.TerrainUpdate(
                windowWidth = tiles.size.toUByte(),
                windowHeight = if (tiles.size > 0) tiles[0].size.toUByte() else 0u,
                windowGridStartPositionX = tiles.let {
                    if (it.size == 0) {
                        return@let 0
                    }
                    if (it[0].size == 0) {
                        return@let 0
                    }
                    it[0][0].gridPosition.x.value.toShort()
                },
                windowGridStartPositionY = tiles.let {
                    if (it.size == 0) {
                        return@let 0
                    }
                    if (it[0].size == 0) {
                        return@let 0
                    }
                    it[0][0].gridPosition.y.value.toShort()
                },
                spriteIds = tiles.flatten().map { it.spriteId }.toTypedArray()
            )
        )
    }

    fun notifyTerrainItemsUpdate(player: Player) {
        egressPacketHandler.notify(
            player.pid,
            EgressDataPacket.TerrainItemsUpdate(
                items = player.getVisibleItems().map {
                    EgressDataPacket.TerrainItemsUpdate.ItemData(
                        itemInstanceId = it.itemInstanceId,
                        type = it.itemDef.type,
                        position = it.position
                    )
                }
            )
        )
    }

    fun notifyEquippedSpellsChange(player: Player) {
        egressPacketHandler.notify(
            player.pid,
            EgressDataPacket.EquippedSpellsUpdate(
                spells = listOf(
                    player.spellsContainer.spell1,
                    player.spellsContainer.spell2,
                    player.spellsContainer.spell3,
                    player.spellsContainer.spell4
                ).map {
                    return@map if (it == null) {
                        null
                    } else {
                        EgressDataPacket.EquippedSpellsUpdate.SpellUpdate(
                            sid = it.sid,
                            name = it.name,
                            spriteId = it.spriteId,
                            cooldown = it.cooldown
                        )
                    }
                }.toTypedArray()
            )
        )
    }

    fun notifySpellUse(to: PID, spellUse: EgressDataPacket.SpellUse) {
        egressPacketHandler.notify(
            to,
            spellUse
        )
    }

    fun notifyDamageTaken(to: PID, damages: Array<EgressDataPacket.DamageTaken.Damage>) {
        egressPacketHandler.notify(
            to,
            EgressDataPacket.DamageTaken(damages)
        )
    }

    fun sendProjectile(to: PID, data: EgressDataPacket.ProjectileSend) {
        egressPacketHandler.notify(to, data)
    }
}
