package clientjvm.infrastructure

import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket
import pl.humberd.udp.server.UdpQueuedPacket
import pl.humberd.udp.server.UdpSendQueue
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentLinkedQueue

class ClientUdpSendQueue : UdpSendQueue {
    private val queue = ConcurrentLinkedQueue<ClientUdpQueuedPacket>()
    private val socketAddress = InetSocketAddress("127.0.0.1", 4445)

    override fun hasNext(): Boolean {
        return !queue.isEmpty()
    }

    override fun popNext(): UdpQueuedPacket {
        return queue.remove()
    }

    fun send(packet: ClientServerUdpPacket) {
        queue.add(ClientUdpQueuedPacket(packet, socketAddress))
    }

}
