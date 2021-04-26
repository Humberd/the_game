package pl.humberd.udp.packets.clientserver

import pl.humberd.models.SID
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket.Type.SPELL_USAGE

data class SpellUsage(
    val sid: SID
) : ClientServerUdpPacket(SPELL_USAGE) {
    constructor(buffer: ReadBuffer): this(
        sid = buffer.getSID()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putSID(sid)
    }
}
