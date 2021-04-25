package pl.humberd.udp.packets.serverclient

import pl.humberd.udp.models.ApiVector2
import pl.humberd.udp.models.CID
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket.Type.CREATURE_POSITION_UPDATE

data class CreaturePositionUpdate(
    val cid: CID,
    val position: ApiVector2
) : ServerClientUdpPacket(CREATURE_POSITION_UPDATE) {
    constructor(buffer: ReadBuffer) : this(
        cid = buffer.getCID(),
        position = buffer.getVector2()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putCID(cid)
        buffer.putVector2(position)
    }
}
