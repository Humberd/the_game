package pl.humberd.udp.packets.clientserver

import pl.humberd.misc.HotPacket
import pl.humberd.models.ApiVector2
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket.Type.POSITION_CHANGE

@HotPacket
data class PositionChange(
    val targetPosition: ApiVector2
) : ClientServerUdpPacket(POSITION_CHANGE) {

    constructor(buffer: ReadBuffer) : this(
        targetPosition = buffer.getVector2()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putVector2(targetPosition)
    }

}
