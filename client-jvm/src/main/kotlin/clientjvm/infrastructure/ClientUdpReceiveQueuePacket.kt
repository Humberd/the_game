package clientjvm.infrastructure

import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket

data class ClientUdpReceiveQueuePacket(
    val packet: ServerClientUdpPacket
)
