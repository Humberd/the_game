package infrastructure.udp.ingress

import infrastructure.udp.UdpClient
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.ByteBuffer

class UdpIngressServer(
    private val socket: DatagramSocket,
    private val packetHandler: UdpIngressPacketHandler
) : Thread("UdpIngressServer") {
    private var buffer = ByteArray(256)

    override fun run() {
        while (true) {
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)

            val byteBuffer = ByteBuffer.wrap(buffer, 0, packet.length)

            packetHandler.handle(byteBuffer, UdpClient(packet.socketAddress))
        }
    }
}
