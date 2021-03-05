package infrastructure.udp.ingress

import core.types.DirectionByte
import core.types.InstanceId
import core.types.PID
import core.types.WorldPosition
import utils.uByte
import utils.uInt
import java.nio.ByteBuffer

enum class IngressPacketType(val value: Int) {
    CONNECTION_HELLO(0x00),
    DISCONNECT(0x01),
    PING_REQUEST(0x02),
    AUTH_LOGIN(0x05),
    POSITION_CHANGE(0x10),
    TERRAIN_ITEM_DRAG(0x11);

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
    data class AuthLogin(val pid: PID) : IngressPacket()
    data class PositionChange(
        val pid: PID,
        val direction: DirectionByte
    ) : IngressPacket() {
        companion object {
            fun from(buffer: ByteBuffer, pid: PID): PositionChange {
                return PositionChange(
                    pid = pid,
                    direction = DirectionByte(buffer.uByte())
                )
            }
        }
    }

    data class TerrainItemDrag(
        val pid: PID,
        val itemInstanceId: InstanceId,
        val targetPosition: WorldPosition
    ) :
        IngressPacket() {
        companion object {
            fun from(buffer: ByteBuffer, pid: PID): TerrainItemDrag {
                return TerrainItemDrag(
                    pid = pid,
                    itemInstanceId = InstanceId(buffer.uInt()),
                    targetPosition = WorldPosition(buffer.float, buffer.float)
                )
            }
        }
    }
}
