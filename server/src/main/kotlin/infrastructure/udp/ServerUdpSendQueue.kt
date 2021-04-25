package infrastructure.udp

import infrastructure.udp.models.ConnectionId
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket
import pl.humberd.udp.server.sender.UdpSendQueue
import java.util.concurrent.ConcurrentLinkedQueue

class ServerUdpSendQueue(
    private val udpClientStore: UdpClientStore
): UdpSendQueue {
    private val queue = ConcurrentLinkedQueue<ServerUdpSendQueuePacket>()

    override fun hasNext() = !queue.isEmpty()

    override fun popNext() = queue.remove()

    fun send(packet: ServerClientUdpPacket, connectionId: ConnectionId) {
        queue.add(ServerUdpSendQueuePacket(packet, connectionId))
    }

}
