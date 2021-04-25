package infrastructure.udp.egress

import core.types.PID
import infrastructure.udp.UdpClientStore
import mu.KotlinLogging
import java.util.concurrent.ConcurrentLinkedQueue

private val logger = KotlinLogging.logger {}


class UdpEgressPacketHandler(
    private val udpClientStore: UdpClientStore
) {
    private val queue = ConcurrentLinkedQueue<EgressPacketFrame>()

    fun hasItems(): Boolean {
        return !queue.isEmpty()
    }

    fun readHead(): EgressPacketFrame {
        return queue.remove()
    }

    fun notify(to: PID, dataPacket: EgressDataPacket) {
        if (!(dataPacket is EgressDataPacket.CreaturePositionUpdate)) {
            logger.debug { "${to} -> ${dataPacket}" }
        }
        val connectionId = udpClientStore.getConnectionIdOrNull(to)
        if (connectionId == null) {
            println("Client not found")
            return
        }

        queue.add(EgressPacketFrame(connectionId, dataPacket))
    }
}
