package infrastructure.udp.egress

import core.maps.ItemType
import core.types.*
import infrastructure.udp.egress.EgressPacketType.*
import utils.*
import java.nio.ByteBuffer

enum class EgressPacketType(val value: Int) {
    CREATURE_UPDATE(0x20),
    CREATURE_DISAPPEAR(0x21),
    CREATURE_POSITION_UPDATE(0x22),
    TERRAIN_UPDATE(0x23),
    TERRAIN_ITEMS_UPDATE(0x24),
    PLAYER_DETAILS(0x25)
}

sealed class EgressDataPacket(
    private val type: EgressPacketType
) {
    fun pack(): ByteArray {
        val buffer = ByteBuffer.allocate(256)
        buffer.put(byteArrayOf(0x42, 0x42, 0x42, 0x42))
        buffer.putUShort(type.value.toUShort())

        packData(buffer)

        return buffer.array()
    }

    protected abstract fun packData(buffer: ByteBuffer)

    data class CreatureUpdate(
        val cid: CID,
        val name: CreatureName,
        val health: UInt,
        val position: WorldPosition,
        val spriteId: SpriteId
    ) : EgressDataPacket(CREATURE_UPDATE) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(cid.value)
            buffer.putString(name.value)
            buffer.putUInt(health)
            buffer.putVector(position)
            buffer.putUShort(spriteId.value)
        }
    }

    data class CreatureDisappear(
        val cid: CID
    ) : EgressDataPacket(CREATURE_DISAPPEAR) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(cid.value)
        }
    }

    data class CreaturePositionUpdate(
        val cid: CID,
        val position: WorldPosition
    ) : EgressDataPacket(CREATURE_POSITION_UPDATE) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(cid.value)
            buffer.putVector(position)
        }
    }

    data class TerrainUpdate(
        val windowWidth: UByte,
        val windowHeight: UByte,
        val windowGridStartPositionX: Short,
        val windowGridStartPositionY: Short,
        val spriteIds: Array<SpriteId>
    ) : EgressDataPacket(TERRAIN_UPDATE) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUByte(windowWidth)
            buffer.putUByte(windowHeight)
            buffer.putShort(windowGridStartPositionX)
            buffer.putShort(windowGridStartPositionY)
            buffer.putArray(spriteIds) {
                buffer.putUShort(it.value)
            }
        }
    }

    data class TerrainItemsUpdate(
        val items: List<ItemData>
    ) : EgressDataPacket(TERRAIN_ITEMS_UPDATE) {
        data class ItemData(
            val iid: IID,
            val type: ItemType,
            val position: WorldPosition
        )

        override fun packData(buffer: ByteBuffer) {
            buffer.putList(items) {
                buffer.putUInt(it.iid.value)
                buffer.putUInt(it.type.id.toUInt())
                buffer.putFloat(it.position.x)
                buffer.putFloat(it.position.y)
            }
        }
    }

    data class PlayerDetails(
        val pid: PID,
        val cid: CID
    ): EgressDataPacket(PLAYER_DETAILS) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(pid.value)
            buffer.putUInt(cid.value)
        }
    }
}
