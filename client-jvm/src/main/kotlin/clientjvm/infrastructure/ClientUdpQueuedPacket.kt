package clientjvm.infrastructure

import mu.KotlinLogging
import pl.humberd.misc.toHex
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket
import pl.humberd.udp.server.UdpQueuedPacket
import java.net.DatagramPacket
import java.net.SocketAddress
import java.nio.ByteBuffer


data class ClientUdpQueuedPacket(
    private val packet: ClientServerUdpPacket,
    private val socketAddress: SocketAddress
) : UdpQueuedPacket {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun toDatagram(): DatagramPacket {
        val buffer = ByteBuffer.allocate(256)
        buffer.putInt(0x69696969)
        packet.pack(WriteBuffer(buffer))
        val rawBytes = buffer.array()
        logger.info { rawBytes.toHex() }

        return DatagramPacket(rawBytes, rawBytes.size, socketAddress)
    }
}
