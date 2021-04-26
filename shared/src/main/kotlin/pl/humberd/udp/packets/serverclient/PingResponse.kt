package pl.humberd.udp.packets.serverclient

import pl.humberd.misc.HotPacket
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

@HotPacket
class PingResponse() : ServerClientUdpPacket(Type.PING_RESPONSE) {
    constructor(buffer: ReadBuffer) : this()

    override fun packData(buffer: WriteBuffer) {
        // nothing
    }
}
