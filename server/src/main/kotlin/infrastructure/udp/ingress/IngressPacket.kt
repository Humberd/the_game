package infrastructure.udp.ingress

import core.types.*
import utils.uInt
import utils.vector
import java.nio.ByteBuffer

enum class IngressPacketType(val value: Int) {
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
        private val map = HashMap<Int, IngressPacketType>()

        init {
            values().forEach { map[it.value] = it }
        }

        fun from(value: Int): IngressPacketType? {
            return map[value]
        }
    }
}

sealed class IngressPacket {
    data class ConnectionHello(val _foo: String = "Connection hello 🖐") : IngressPacket()
    data class Disconnect(val pid: PID?) : IngressPacket()
    data class PingRequest(val pid: PID): IngressPacket()
    data class AuthLogin(val pid: PID) : IngressPacket()
    data class PositionChange(
        val pid: PID,
        val targetPosition: WorldPosition
    ) : IngressPacket() {
        companion object {
            fun from(buffer: ByteBuffer, pid: PID): PositionChange {
                return PositionChange(
                    pid = pid,
                    targetPosition = buffer.vector()
                )
            }
        }
    }

    data class TerrainItemDrag(
        val pid: PID,
        val itemInstanceId: ItemInstanceId,
        val targetPosition: WorldPosition
    ) : IngressPacket() {
        companion object {
            fun from(buffer: ByteBuffer, pid: PID): TerrainItemDrag {
                return TerrainItemDrag(
                    pid = pid,
                    itemInstanceId = ItemInstanceId(buffer.uInt()),
                    targetPosition = WorldPosition(buffer.float, buffer.float)
                )
            }
        }
    }

    data class SpellUsage(
        val pid: PID,
        val sid: SID
    ) : IngressPacket() {
        companion object {
            fun from(buffer: ByteBuffer, pid: PID): SpellUsage {
                return SpellUsage(
                    pid = pid,
                    sid = SID(buffer.uInt())
                )
            }
        }
    }

    data class BasicAttackStart(
        val pid: PID,
        val targetCid: CID
    ) : IngressPacket() {
        companion object {
            fun from(buffer: ByteBuffer, pid: PID): BasicAttackStart {
                return BasicAttackStart(
                    pid = pid,
                    targetCid = CID(buffer.uInt())
                )
            }
        }
    }

    data class BasicAttackStop(
        val pid: PID
    ) : IngressPacket() {
        companion object {
            fun from(buffer: ByteBuffer, pid: PID): BasicAttackStop {
                return BasicAttackStop(
                    pid = pid
                )
            }
        }
    }

    data class PlayerStatsUpdateRequest(
        val pid: PID
    ) : IngressPacket() {
        companion object {
            fun from(buffer: ByteBuffer, pid: PID) = PlayerStatsUpdateRequest(pid)
        }
    }
}
