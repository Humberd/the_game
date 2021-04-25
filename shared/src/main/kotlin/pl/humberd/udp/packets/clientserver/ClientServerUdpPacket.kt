package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.UdpPacket
import pl.humberd.udp.packets.UdpPacketType

sealed class ClientServerUdpPacket(type: Type) : UdpPacket<ClientServerUdpPacket.Type>(type) {
    enum class Type(
        override val value: Int,
        val serialize: (buffer: ReadBuffer) -> ClientServerUdpPacket = { TODO() }
    ) : UdpPacketType {
        CONNECTION_HELLO(0x00, { ConnectionHello(it) }),
        DISCONNECT(0x01, { Disconnect(it) }),
        PING_REQUEST(0x02),
        AUTH_LOGIN(0x05, { AuthLogin(it) }),
        POSITION_CHANGE(0x10, { PositionChange(it) }),
        SPELL_USAGE(0x12),
        BASIC_ATTACK_START(0x13, { BasicAttackStart(it) }),
        BASIC_ATTACK_END(0x14, { BasicAttackEnd(it) }),
        PLAYER_STATS_UPDATE_REQUEST(0x15, { PlayerStatsUpdateRequest(it) });

        companion object {
            private val map = HashMap<Int, Type>()

            init {
                values().forEach { map[it.value] = it }
            }

            fun from(value: Int): Type? {
                return map[value]
            }
        }
    }
}
