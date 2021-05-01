package clientjvm.global

import clientjvm.infrastructure.ClientUdpSendQueue
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket
import pl.humberd.udp.server.sender.UdpSenderService

object ClientDataSender {
    private val sendQueue = ClientUdpSendQueue()
    private lateinit var udpSenderService: UdpSenderService

    fun init() {
        udpSenderService = UdpSenderService(socket, sendQueue)
        udpSenderService.start()
    }

    fun kill() {
        udpSenderService.kill()
    }

    fun send(packet: ClientServerUdpPacket) {
        sendQueue.send(packet)
    }
}
