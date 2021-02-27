package infrastructure.egress

import infrastructure.UdpClient
import java.net.DatagramPacket

data class EgressPacketFrame(
    val client: UdpClient,
    val data: EgressDataPacket
) {
    fun prepareDatagram(): DatagramPacket {
        val buffer = data.pack()

        return DatagramPacket(buffer, buffer.size, client.address)
    }
}
