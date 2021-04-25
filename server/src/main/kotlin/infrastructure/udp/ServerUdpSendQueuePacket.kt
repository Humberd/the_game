package infrastructure.udp

import infrastructure.udp.models.ConnectionId
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket
import pl.humberd.udp.server.sender.UdpSendQueuedPacket
import java.net.DatagramPacket
import java.nio.ByteBuffer

data class ServerUdpSendQueuePacket(
    private val packet: ServerClientUdpPacket,
    private val connectionId: ConnectionId
) : UdpSendQueuedPacket {
    override fun toDatagram(buffer: ByteBuffer): DatagramPacket {
        buffer.putInt(0x42424242)
        packet.pack(WriteBuffer(buffer))

        return DatagramPacket(buffer.array(), buffer.position(), connectionId.socketAddress)
    }
}
