package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket.Type.AUTH_LOGIN

data class AuthLogin(
    // fixme, user should send account name and password
    val pid: UInt
) : ClientServerUdpPacket(AUTH_LOGIN) {
    constructor(buffer: ReadBuffer) : this(
        pid = buffer.getUInt()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putUInt(pid)
    }
}
