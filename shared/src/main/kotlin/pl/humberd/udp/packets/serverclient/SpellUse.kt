package pl.humberd.udp.packets.serverclient

import pl.humberd.models.ApiVector2
import pl.humberd.models.Milliseconds
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

data class SpellUse(
    val sourcePosition: ApiVector2,
    val effects: Array<SpellEffect>
): ServerClientUdpPacket(Type.SPELL_USE) {
    data class SpellEffect(
        // fixme spriteId
        val spriteId: UShort,
        val duration: Milliseconds
    )
    constructor(buffer: ReadBuffer): this(
        sourcePosition = buffer.getVector2(),
        effects = buffer.getArray {
            SpellEffect(
                spriteId = buffer.getUShort(),
                duration = buffer.getMilliseconds()
            )
        }
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putVector2(sourcePosition)
        buffer.putArray(effects) {
            buffer.putUShort(it.spriteId)
            buffer.putMilliseconds(it.duration)
        }
    }
}
