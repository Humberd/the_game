package infrastructure.udp.egress

import infrastructure.udp.models.ConnectionId
import java.net.DatagramPacket

data class EgressPacketFrame(
    val connectionId: ConnectionId,
    val data: EgressDataPacket
) {
    fun prepareDatagram(): DatagramPacket {
        val buffer = data.pack()

        return DatagramPacket(buffer, buffer.size, connectionId.socketAddress)
    }
}
