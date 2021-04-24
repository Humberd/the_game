package clientjvm.global

import clientjvm.infrastructure.ClientUdpSendQueue
import godot.core.memory.GodotStatic
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket
import pl.humberd.udp.server.UdpSenderServer
import java.net.DatagramSocket

object ClientDataSender : GodotStatic {
    private val sendQueue = ClientUdpSendQueue()
    private val udpSenderServer: UdpSenderServer

    init {
        registerAsSingleton()

        udpSenderServer = UdpSenderServer(DatagramSocket(), sendQueue)

        udpSenderServer.start()
    }

    override fun collect() {
        udpSenderServer.kill()
    }

    fun send(packet: ClientServerUdpPacket) {
        sendQueue.send(packet)
    }
}
