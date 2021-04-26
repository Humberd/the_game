package pl.humberd.udp.packets.serverclient

import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.WriteBuffer

data class CreatureStatsUpdate(
    val defense: CreatureIntStatPacket,
    val attack: CreatureIntStatPacket,
    val attackSpeed: CreatureFloatStatPacket,
    val movementSpeed: CreatureFloatStatPacket,
    val healthPool: CreatureIntStatPacket,
) : ServerClientUdpPacket(Type.CREATURE_STATS_UPDATE) {
    data class CreatureIntStatPacket(
        val base: Int,
        val current: Int
    )

    data class CreatureFloatStatPacket(
        val base: Float,
        val current: Float
    )

    private companion object {
        fun getIntStat(buffer: ReadBuffer) =
            CreatureIntStatPacket(
                base = buffer.getInt(),
                current = buffer.getInt()
            )

        fun getFloatStat(buffer: ReadBuffer) =
            CreatureFloatStatPacket(
                base = buffer.getFloat(),
                current = buffer.getFloat()
            )

        fun putStat(buffer: WriteBuffer, stat: CreatureIntStatPacket) {
            buffer.putInt(stat.base)
            buffer.putInt(stat.current)
        }

        fun putStat(buffer: WriteBuffer, stat: CreatureFloatStatPacket) {
            buffer.putFloat(stat.base)
            buffer.putFloat(stat.current)
        }
    }

    constructor(buffer: ReadBuffer) : this(
        defense = getIntStat(buffer),
        attack = getIntStat(buffer),
        attackSpeed = getFloatStat(buffer),
        movementSpeed = getFloatStat(buffer),
        healthPool = getIntStat(buffer)
    )

    override fun packData(buffer: WriteBuffer) {
        putStat(buffer, defense)
        putStat(buffer, attack)
        putStat(buffer, attackSpeed)
        putStat(buffer, movementSpeed)
        putStat(buffer, healthPool)
    }
}
