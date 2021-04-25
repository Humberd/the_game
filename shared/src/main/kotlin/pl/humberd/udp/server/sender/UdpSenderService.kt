package pl.humberd.udp.server.sender

import pl.humberd.udp.server.UdpServer
import java.net.DatagramSocket
import java.nio.ByteBuffer

class UdpSenderService(
    private val socket: DatagramSocket,
    private val sendQueue: UdpSendQueue
) : UdpServer("UpdSenderServer") {
    override fun onTick(buffer: ByteBuffer) {
        if (!sendQueue.hasNext()) {
            return
        }

        socket.send(sendQueue.popNext().toDatagram(buffer))
    }
}
