package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

class AuthLogin() : ClientServerUdpPacket(ClientServerUdpPacketType.AUTH_LOGIN) {
    constructor(buffer: ReadBuffer) : this()

    override fun packData(buffer: WriteBuffer) {
        // nothing to pack
    }
}
