package infrastructure.egress

import core.types.PID
import infrastructure.egress.EgressPacketType.PLAYER_DISCONNECT
import infrastructure.egress.EgressPacketType.PLAYER_POSITION_UPDATE
import infrastructure.egress.EgressPacketType.PLAYER_UPDATE
import org.mini2Dx.gdx.math.Vector2
import utils.putUInt
import java.nio.ByteBuffer

object EgressPacketType {
    const val PLAYER_UPDATE = 0x20
    const val PLAYER_DISCONNECT = 0x21
    const val PLAYER_POSITION_UPDATE = 0x22
}

sealed class EgressDataPacket(
    private val type: Int
) {
    fun pack(): ByteArray {
        val buffer = ByteBuffer.allocate(256)
        buffer.put(byteArrayOf(0x42, 0x42, 0x42, 0x42))
        buffer.putShort(type.toShort())

        packData(buffer)

        return buffer.array()
    }

    protected abstract fun packData(buffer: ByteBuffer)

    data class PlayerUpdate(
        val pid: PID,
        val position: Vector2
    ) : EgressDataPacket(PLAYER_UPDATE) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(pid.value)
            buffer.putFloat(position.x)
            buffer.putFloat(position.y)
        }
    }

    data class PlayerDisconnect(
        val pid: PID
    ) : EgressDataPacket(PLAYER_DISCONNECT) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(pid.value)
        }
    }

    data class PlayerPositionUpdate(
        val pid: PID,
        val position: Vector2
    ): EgressDataPacket(PLAYER_POSITION_UPDATE) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(pid.value)
            buffer.putFloat(position.x)
            buffer.putFloat(position.y)
        }
    }
}
