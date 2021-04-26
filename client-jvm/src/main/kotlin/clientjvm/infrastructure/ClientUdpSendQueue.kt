package clientjvm.infrastructure

import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket
import pl.humberd.udp.server.sender.UdpSendQueue
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentLinkedQueue

class ClientUdpSendQueue : UdpSendQueue {
    private val queue = ConcurrentLinkedQueue<ClientUdpSendQueuePacket>()
    private val socketAddress = InetSocketAddress("127.0.0.1", 4445)

    override fun hasNext() = !queue.isEmpty()

    override fun popNext() = queue.remove()

    fun send(packet: ClientServerUdpPacket) {
        queue.add(ClientUdpSendQueuePacket(packet, socketAddress))
    }

}
