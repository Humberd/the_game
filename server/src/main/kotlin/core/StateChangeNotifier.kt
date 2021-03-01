package core

import core.types.PID
import infrastructure.udp.egress.EgressDataPacket
import infrastructure.udp.egress.UdpEgressPacketHandler

class StateChangeNotifier(
    private val egressPacketHandler: UdpEgressPacketHandler
) {
    fun notifyPlayerUpdate(to: PID, playerCharacter: PlayerCharacter) {
        egressPacketHandler.requestSend(
            to, EgressDataPacket.PlayerUpdate(
                pid = playerCharacter.id,
                position = playerCharacter.position,
                health = playerCharacter.health,
                name = playerCharacter.name
            )
        )
    }

    fun notifyPlayerDisconnect(to: PID, pid: PID) {
        egressPacketHandler.requestSend(
            to, EgressDataPacket.PlayerDisconnect(pid)
        )
    }

    fun notifyPlayerPositionUpdate(to: PID, playerCharacter: PlayerCharacter) {
        egressPacketHandler.requestSend(
            to, EgressDataPacket.PlayerPositionUpdate(
                playerCharacter.id,
                position = playerCharacter.position
            )
        )
    }
}
