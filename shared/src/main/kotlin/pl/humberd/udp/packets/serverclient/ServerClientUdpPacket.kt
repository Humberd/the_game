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
        CREATURE_UPDATE(0x20, { CreatureUpdate(it) }),
        CREATURE_DISAPPEAR(0x21, { CreatureDisappear(it) }),
        CREATURE_POSITION_UPDATE(0x22, { CreaturePositionUpdate(it) }),
        TERRAIN_UPDATE(0x23, { TerrainUpdate(it) }),
        PLAYER_DETAILS(0x25, { PlayerDetails(it) }),
        EQUIPPED_SPELLS_UPDATE(0x26, { EquippedSpellsUpdate(it) }),
        SPELL_USE(0x27, { SpellUse(it) }),
        DAMAGE_TAKEN(0x28),
        PROJECTILE_SEND(0x29),
        EQUIPMENT_UPDATE(0x2A),
        CREATURE_STATS_UPDATE(0x2B),
        BACKPACK_UPDATE(0x2C),
        PING_RESPONSE(0x2D),
        TERRAIN_WALLS_UPDATE(0x2E);


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
