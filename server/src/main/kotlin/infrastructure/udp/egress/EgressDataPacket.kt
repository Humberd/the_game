package infrastructure.udp.egress

import core.PlayerName
import core.types.PID
import core.types.SpriteId
import infrastructure.udp.egress.EgressPacketType.*
import org.mini2Dx.gdx.math.Vector2
import utils.putString
import utils.putUByte
import utils.putUInt
import utils.putUShort
import java.nio.ByteBuffer

enum class EgressPacketType(val value: Int) {
    PLAYER_UPDATE(0x20),
    PLAYER_DISCONNECT(0x21),
    PLAYER_POSITION_UPDATE(0x22),
    TERRAIN_UPDATE(0x23);
}

sealed class EgressDataPacket(
    private val type: EgressPacketType
) {
    fun pack(): ByteArray {
        val buffer = ByteBuffer.allocate(256)
        buffer.put(byteArrayOf(0x42, 0x42, 0x42, 0x42))
        buffer.putShort(type.value.toShort())

        packData(buffer)

        return buffer.array()
    }

    protected abstract fun packData(buffer: ByteBuffer)

    data class PlayerUpdate(
        val pid: PID,
        val position: Vector2,
        val health: UInt,
        val name: PlayerName,
    ) : EgressDataPacket(PLAYER_UPDATE) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(pid.value)
            buffer.putFloat(position.x)
            buffer.putFloat(position.y)
            buffer.putUInt(health)
            buffer.putString(name.value)
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

    data class TerrainUpdate(
        val windowWidth: UByte,
        val windowHeight: UByte,
        val windowGridStartPositionX: Short,
        val windowGridStartPositionY: Short,
        val spriteIds: Array<SpriteId>
    ): EgressDataPacket(TERRAIN_UPDATE) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUByte(windowWidth)
            buffer.putUByte(windowHeight)
            buffer.putShort(windowGridStartPositionX)
            buffer.putShort(windowGridStartPositionY)

            buffer.putUShort(spriteIds.size.toUShort())
            spriteIds.forEach {
                buffer.putUShort(it.value)
            }
        }
    }
}
