package infrastructure.udp.ingress

import infrastructure.udp.UdpClient
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.ByteBuffer

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


class UdpIngressServer(
    private val socket: DatagramSocket,
    private val packetHandler: UdpIngressPacketHandler
) : Thread("UdpIngressServer") {
    private var buffer = ByteArray(256)

    override fun run() {
        logger.info { "Starting Udp Ingress Server" }

        while (true) {
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)

            val byteBuffer = ByteBuffer.wrap(buffer, 0, packet.length)

            packetHandler.handle(byteBuffer, UdpClient(packet.socketAddress))
        }
    }
}
