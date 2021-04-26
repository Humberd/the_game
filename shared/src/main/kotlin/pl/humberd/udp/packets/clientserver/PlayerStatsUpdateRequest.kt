package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket.Type.PLAYER_STATS_UPDATE_REQUEST

class PlayerStatsUpdateRequest() : ClientServerUdpPacket(PLAYER_STATS_UPDATE_REQUEST) {
    constructor(buffer: ReadBuffer) : this()

    override fun packData(buffer: WriteBuffer) {
        // nothing
    }

    override fun toString() = "PlayerStatsUpdateRequest()"
}
