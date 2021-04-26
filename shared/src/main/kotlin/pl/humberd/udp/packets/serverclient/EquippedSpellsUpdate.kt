package pl.humberd.udp.packets.serverclient

import pl.humberd.models.Milliseconds
import pl.humberd.models.SID
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

data class EquippedSpellsUpdate(
    val spells: Array<SpellUpdate?>
) : ServerClientUdpPacket(Type.EQUIPPED_SPELLS_UPDATE) {
    data class SpellUpdate(
        val sid: SID,
        val name: String,
        // fixme: spriteId
        val spriteId: UShort,
        val cooldown: Milliseconds
    )

    constructor(buffer: ReadBuffer) : this(
        spells = buffer.getNullableArray {
            SpellUpdate(
                sid = buffer.getSID(),
                name = buffer.getString(),
                spriteId = buffer.getUShort(),
                cooldown = buffer.getMilliseconds()
            )
        }
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putNullableArray(spells) {
            buffer.putSID(it.sid)
            buffer.putString(it.name)
            buffer.putUShort(it.spriteId)
            buffer.putMilliseconds(it.cooldown)
        }
    }
}
