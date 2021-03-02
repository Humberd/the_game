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

    fun notifyTerrainUpdate(player: PlayerCharacter, map: GameMap) {
        val gridPosition = map.toGridPosition(player.position)
        val tiles = map.getTilesAround(gridPosition, player.viewRadius.toInt())

        egressPacketHandler.notify(
            player.id,
            EgressDataPacket.TerrainUpdate(
                windowWidth = player.viewRadius,
                windowHeight = player.viewRadius,
                windowGridStartPositionX = tiles[0].gridPosition.x.value.toShort(),
                windowGridStartPositionY = tiles[0].gridPosition.y.value.toShort(),
                spriteIds = tiles.map { it.spriteId }.toTypedArray()
            )
        )
    }
}
