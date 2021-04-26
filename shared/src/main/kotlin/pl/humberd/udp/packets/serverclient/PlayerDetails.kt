package pl.humberd.udp.packets.serverclient

import pl.humberd.models.CID
import pl.humberd.models.PID
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

data class PlayerDetails(
    val pid: PID,
    val cid: CID
): ServerClientUdpPacket(Type.PLAYER_DETAILS) {
    constructor(buffer: ReadBuffer): this(
        pid = buffer.getPID(),
        cid = buffer.getCID()
    )

    override fun packData(buffer: WriteBuffer) {
        buffer.putPID(pid)
        buffer.putCID(cid)
    }
}
