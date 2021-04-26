package clientjvm.infrastructure

import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket
import pl.humberd.udp.server.sender.UdpSendQueuedPacket
import java.net.DatagramPacket
import java.net.SocketAddress
import java.nio.ByteBuffer

data class ClientUdpSendQueuePacket(
    private val packet: ClientServerUdpPacket,
    private val socketAddress: SocketAddress
) : UdpSendQueuedPacket {

    override fun toDatagram(buffer: ByteBuffer): DatagramPacket {
        buffer.putInt(0x69696969)
        packet.pack(WriteBuffer(buffer))

        return DatagramPacket(buffer.array(), buffer.position(), socketAddress)
    }
}
