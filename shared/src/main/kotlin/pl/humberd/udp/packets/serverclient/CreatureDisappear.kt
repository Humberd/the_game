package pl.humberd.udp.packets.serverclient

import pl.humberd.models.CID
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket.Type.CREATURE_DISAPPEAR

data class CreatureDisappear(
    val cid: CID
) : ServerClientUdpPacket(CREATURE_DISAPPEAR) {
    constructor(buffer: ReadBuffer) : this(
        cid = buffer.getCID()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putCID(cid)
    }
}
