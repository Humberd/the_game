package pl.humberd.udp.packets.serverclient

import pl.humberd.models.ApiVector2
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

data class DamageTaken(
    val damages: Array<Damage>
) : ServerClientUdpPacket(Type.DAMAGE_TAKEN) {
    data class Damage(
        val position: ApiVector2,
        val amount: UInt
    )

    constructor(buffer: ReadBuffer) : this(
        damages = buffer.getArray {
            Damage(
                position = buffer.getVector2(),
                amount = buffer.getUInt()
            )
        }
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putArray(damages) {
            buffer.putVector2(it.position)
            buffer.putUInt(it.amount)
        }
    }
}
