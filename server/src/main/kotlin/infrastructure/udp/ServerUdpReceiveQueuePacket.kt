package infrastructure.udp

import infrastructure.udp.models.ConnectionId
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket

data class ServerUdpReceiveQueuePacket(
    val packet: ClientServerUdpPacket,
    val connectionId: ConnectionId
)
