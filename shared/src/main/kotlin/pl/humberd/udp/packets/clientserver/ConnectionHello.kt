package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket.Type.CONNECTION_HELLO

class ConnectionHello() : ClientServerUdpPacket(CONNECTION_HELLO) {
    constructor(buffer: ReadBuffer) : this()

    override fun packData(buffer: WriteBuffer) {
        // nothing to pack
    }

    override fun toString() = "ConnectionHello()"
}
