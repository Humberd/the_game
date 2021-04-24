package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.models.ApiVector2
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

data class PositionChange(
    val targetPosition: ApiVector2
) : ClientServerUdpPacket(ClientServerUdpPacketType.POSITION_CHANGE) {

    constructor(buffer: ReadBuffer) : this(
        targetPosition = buffer.getVector2()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putVector2(targetPosition)
    }

}
