package pl.humberd.udp.packets.serverclient

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.UdpPacket
import pl.humberd.udp.packets.UdpPacketType
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket.Type

sealed class ServerClientUdpPacket(type: Type) : UdpPacket<Type>(type) {
    enum class Type(
        override val value: Int,
        val serialize: (buffer: ReadBuffer) -> ServerClientUdpPacket = { TODO() }
    ) : UdpPacketType {
        CREATURE_UPDATE(0x20),
        CREATURE_DISAPPEAR(0x21),
        CREATURE_POSITION_UPDATE(0x22),
        TERRAIN_UPDATE(0x23),
        TERRAIN_ITEMS_UPDATE(0x24),
        PLAYER_DETAILS(0x25),
        EQUIPPED_SPELLS_UPDATE(0x26),
        SPELL_USE(0x27),
        DAMAGE_TAKEN(0x28),
        PROJECTILE_SEND(0x29),
        EQUIPMENT_UPDATE(0x2A),
        CREATURE_STATS_UPDATE(0x2B),
        BACKPACK_UPDATE(0x2C);


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
