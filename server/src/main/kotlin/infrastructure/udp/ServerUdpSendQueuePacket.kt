package infrastructure.udp

import infrastructure.udp.models.ConnectionId
import mu.KotlinLogging
import pl.humberd.misc.isHot
import pl.humberd.misc.toHex
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket
import pl.humberd.udp.server.sender.UdpSendQueuedPacket
import java.net.DatagramPacket
import java.nio.ByteBuffer

data class ServerUdpSendQueuePacket(
    private val packet: ServerClientUdpPacket,
    private val connectionId: ConnectionId
) : UdpSendQueuedPacket {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun toDatagram(buffer: ByteBuffer): DatagramPacket {
        buffer.putInt(0x42424242)
        packet.pack(WriteBuffer(buffer))

        if (!packet.isHot()) {
            logger.info { "---SEND---" }
            logger.info { "$packet" }
            logger.info {
                "$connectionId (${buffer.position()}B) ${buffer.array().sliceArray(0..buffer.position() - 1).toHex()}"
            }
        }
        return DatagramPacket(buffer.array(), buffer.position(), connectionId.socketAddress)
    }
}
