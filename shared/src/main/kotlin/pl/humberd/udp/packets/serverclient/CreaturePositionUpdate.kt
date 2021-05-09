package pl.humberd.udp.packets.serverclient

import pl.humberd.misc.HotPacket
import pl.humberd.models.ApiVector2
import pl.humberd.models.CID
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket.Type.CREATURE_POSITION_UPDATE

@HotPacket
data class CreaturePositionUpdate(
    val cid: CID,
    val position: ApiVector2,
    val rotation: Float
) : ServerClientUdpPacket(CREATURE_POSITION_UPDATE) {
    constructor(buffer: ReadBuffer) : this(
        cid = buffer.getCID(),
        position = buffer.getVector2(),
        rotation = buffer.getFloat()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putCID(cid)
        buffer.putVector2(position)
        buffer.putFloat(rotation)
    }
}

