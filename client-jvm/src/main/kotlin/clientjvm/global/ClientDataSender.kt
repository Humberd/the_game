package clientjvm.global

import clientjvm.infrastructure.ClientUdpSendQueue
import godot.core.memory.GodotStatic
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket
import pl.humberd.udp.server.sender.UdpSenderService

object ClientDataSender : GodotStatic {
    private val sendQueue = ClientUdpSendQueue()
    private val udpSenderService: UdpSenderService

    init {
        registerAsSingleton()

        udpSenderService = UdpSenderService(socket, sendQueue)
        udpSenderService.start()
    }

    override fun collect() {
        udpSenderService.kill()
    }

    fun send(packet: ClientServerUdpPacket) {
        sendQueue.send(packet)
    }
}
