package pl.humberd.udp.packets.clientserver

import pl.humberd.models.ApiVector2
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket.Type.SPELL_CAST_START

data class SpellCastStart(
    val spellSlot: UByte,
    val targetPosition: ApiVector2
) : ClientServerUdpPacket(SPELL_CAST_START) {
    constructor(buffer: ReadBuffer) : this(
        spellSlot = buffer.getUByte(),
        targetPosition = buffer.getVector2()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putUByte(spellSlot)
        buffer.putVector2(targetPosition)
    }
}
