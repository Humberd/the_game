package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.models.CID
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket.Type.BASIC_ATTACK_START

data class BasicAttackStart(
    val targetCid: CID
): ClientServerUdpPacket(BASIC_ATTACK_START) {
    constructor(buffer: ReadBuffer): this(
        targetCid = buffer.getCID()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putCID(targetCid)
    }
}
