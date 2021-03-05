package core

import core.maps.GameMap
import core.types.PID
import infrastructure.udp.egress.EgressDataPacket
import infrastructure.udp.egress.UdpEgressPacketHandler

class StateChangeNotifier(
    private val egressPacketHandler: UdpEgressPacketHandler
) {
    fun notifyPlayerUpdate(to: PID, playerCharacter: PlayerCharacter) {
        egressPacketHandler.notify(
            to,
            EgressDataPacket.PlayerUpdate(
                pid = playerCharacter.id,
                position = playerCharacter.position,
                health = playerCharacter.health,
                name = playerCharacter.name
            )
        )
    }

    fun notifyPlayerDisconnect(to: PID, pid: PID) {
        egressPacketHandler.notify(
            to,
            EgressDataPacket.PlayerDisconnect(pid)
        )
    }

    fun notifyPlayerPositionUpdate(to: PID, playerCharacter: PlayerCharacter) {
        egressPacketHandler.notify(
            to,
            EgressDataPacket.PlayerPositionUpdate(
                playerCharacter.id,
                position = playerCharacter.position
            )
        )
    }

    fun notifyTerrainUpdate(player: PlayerCharacter) {
        val tiles = player.lastUpdate.tileSlice

        egressPacketHandler.notify(
            player.id,
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

    fun notifyTerrainItemsUpdate(player: PlayerCharacter) {
        egressPacketHandler.notify(
            player.id,
            EgressDataPacket.TerrainItemsUpdate(
                items = player.getVisibleItems().map {
                    EgressDataPacket.TerrainItemsUpdate.ItemData(
                        instanceId = it.instanceId,
                        itemId = it.itemDef.id,
                        position = it.position
                    )
                }
            )
        )
    }
}
