package core

import core.types.PID
import infrastructure.egress.EgressDataPacket
import infrastructure.egress.UdpEgressPacketHandler
import org.mini2Dx.gdx.math.Vector2

class StateChangeNotifier(
    private val egressPacketHandler: UdpEgressPacketHandler
) {
    fun notifyPlayerUpdate(to: PID, playerCharacter: PlayerCharacter) {
        egressPacketHandler.requestSend(
            to, EgressDataPacket.PlayerUpdate(
                playerCharacter.id,
                position = playerCharacter.position
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
