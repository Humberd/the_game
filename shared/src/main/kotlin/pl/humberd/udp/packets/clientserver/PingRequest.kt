package pl.humberd.udp.packets.clientserver

import pl.humberd.misc.HotPacket
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket.Type.PING_REQUEST

@HotPacket
class PingRequest() : ClientServerUdpPacket(PING_REQUEST) {
    constructor(buffer: ReadBuffer) : this()

    override fun packData(buffer: WriteBuffer) {
        // nothing
    }

    override fun toString() = "PingRequest()"
}
