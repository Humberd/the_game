package infrastructure.udp.egress

import core.types.PID
import infrastructure.udp.UdpClientStore
import java.util.concurrent.ConcurrentLinkedQueue

import mu.KotlinLogging

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
        logger.debug { "Requesting data send to ${to} -> ${dataPacket}" }
        val client = udpClientStore.getClient(to)
        if (client == null) {
            println("Client not found")
            return
        }

        queue.add(EgressPacketFrame(client, dataPacket))
    }
}
