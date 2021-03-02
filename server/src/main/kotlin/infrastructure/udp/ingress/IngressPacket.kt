package infrastructure.udp.ingress

import core.types.DirectionByte
import core.types.PID
import utils.uByte
import java.nio.ByteBuffer

enum class IngressPacketType(val value: Int) {
    CONNECTION_HELLO(0x00),
    DISCONNECT(0x01),
    PING_REQUEST(0x02),
    AUTH_LOGIN(0x05),
    POSITION_CHANGE(0x10);

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
    data class PositionChange(val pid: PID, val direction: DirectionByte) : IngressPacket() {
        companion object {
            fun from(buffer: ByteBuffer, pid: PID): PositionChange {
                return PositionChange(
                    pid = pid,
                    direction = DirectionByte(buffer.uByte())
                )
            }
        }
    }
}
