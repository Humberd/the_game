package pl.humberd.udp.packets.clientserver

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.UdpPacket
import pl.humberd.udp.packets.UdpPacketType
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket.Type

sealed class ClientServerUdpPacket(type: Type) : UdpPacket<Type>(type) {
    enum class Type(
        override val value: Int,
        val serialize: (buffer: ReadBuffer) -> ClientServerUdpPacket
    ) : UdpPacketType {
        CONNECTION_HELLO(0x00, { ConnectionHello(it) }),
        DISCONNECT(0x01, { Disconnect(it) }),
        PING_REQUEST(0x02, { PingRequest(it) }),
        AUTH_LOGIN(0x05, { AuthLogin(it) }),
        POSITION_CHANGE(0x10, { PositionChange(it) }),
        SPELL_USAGE(0x12, { SpellUsage(it) }),
        BASIC_ATTACK_START(0x13, { BasicAttackStart(it) }),
        BASIC_ATTACK_END(0x14, { BasicAttackEnd(it) }),
        PLAYER_STATS_UPDATE_REQUEST(0x15, { PlayerStatsUpdateRequest(it) }),
        SPELL_CAST_START(0x16, { SpellCastStart(it) }),
        SPELL_CAST_END(0x17, { SpellCastEnd(it) });

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
