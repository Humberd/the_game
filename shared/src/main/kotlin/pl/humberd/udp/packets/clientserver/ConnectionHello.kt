package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

class ConnectionHello() : ClientServerUdpPacket(ClientServerUdpPacketType.CONNECTION_HELLO) {
    constructor(buffer: ReadBuffer) : this()

    override fun packData(buffer: WriteBuffer) {
        // nothing to pack
    }
}
