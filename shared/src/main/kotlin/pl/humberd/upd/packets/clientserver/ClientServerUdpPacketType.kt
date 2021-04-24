package pl.humberd.upd.packets.clientserver

import pl.humberd.upd.packets.UdpPacketType

enum class ClientServerUdpPacketType(override val value: Int) : UdpPacketType {
    CONNECTION_HELLO(0x00),
    DISCONNECT(0x01),
    PING_REQUEST(0x02),
    AUTH_LOGIN(0x05),
    POSITION_CHANGE(0x10),
    TERRAIN_ITEM_DRAG(0x11),
    SPELL_USAGE(0x12),
    BASIC_ATTACK_START(0x13),
    BASIC_ATTACK_END(0x14),
    PLAYER_STATS_UPDATE_REQUEST(0x15);

    companion object {
        private val map = HashMap<Int, ClientServerUdpPacketType>()

        init {
            values().forEach { map[it.value] = it }
        }

        fun from(value: Int): ClientServerUdpPacketType? {
            return map[value]
        }
    }
}
