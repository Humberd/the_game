package pl.humberd.udp.packets.serverclient

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

class PingResponse() : ServerClientUdpPacket(Type.PING_RESPONSE) {
    constructor(buffer: ReadBuffer) : this()

    override fun packData(buffer: WriteBuffer) {
        // nothing
    }

    override fun toString() = "PingResponse()"
}
