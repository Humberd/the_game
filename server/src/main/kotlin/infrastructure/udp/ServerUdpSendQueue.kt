package infrastructure.udp

import mu.KotlinLogging
import pl.humberd.models.PID
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket
import pl.humberd.udp.server.sender.UdpSendQueue
import java.util.concurrent.ConcurrentLinkedQueue

class ServerUdpSendQueue(
    private val udpClientStore: UdpClientStore
) : UdpSendQueue {

    companion object {
        private val logger = KotlinLogging.logger {}

    }
    private val queue = ConcurrentLinkedQueue<ServerUdpSendQueuePacket>()

    override fun hasNext() = !queue.isEmpty()

    override fun popNext() = queue.remove()

    fun put(pid: PID, packet: ServerClientUdpPacket) {
        val connectionId = udpClientStore.getConnectionIdOrNull(pid)
        if (connectionId == null) {
            logger.info { "ConnectionId not found ${pid}" }
            return
        }

        queue.add(ServerUdpSendQueuePacket(packet, connectionId))
    }

}
