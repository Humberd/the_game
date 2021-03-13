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
    PLAYER_DETAILS(0x25),
    EQUIPPED_SPELLS_UPDATE(0x26),
    SPELL_USE(0x27),
    DAMAGE_TAKEN(0x28)
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
        val baseHealth: UInt,
        val currentHealth: UInt,
        val position: WorldPosition,
        val spriteId: SpriteId,
        val bodyRadius: Float,
        val attackTriggerRadius: Float,
    ) : EgressDataPacket(CREATURE_UPDATE) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(cid.value)
            buffer.putString(name.value)
            buffer.putUInt(baseHealth)
            buffer.putUInt(currentHealth)
            buffer.putVector(position)
            buffer.putUShort(spriteId.value)
            buffer.putFloat(bodyRadius)
            buffer.putFloat(attackTriggerRadius)
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
    ) : EgressDataPacket(PLAYER_DETAILS) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(pid.value)
            buffer.putUInt(cid.value)
        }
    }

    data class EquippedSpellsUpdate(
        val spells: Array<SpellUpdate?>
    ) : EgressDataPacket(EQUIPPED_SPELLS_UPDATE) {
        data class SpellUpdate(
            val sid: SID,
            val name: String,
            val spriteId: SpriteId,
            val cooldown: Milliseconds
        )

        override fun packData(buffer: ByteBuffer) {
            buffer.putArray(spells) {
                if (it == null) {
                    buffer.putUInt(0u)
                    return@putArray
                }
                buffer.putUInt(it.sid.value)
                buffer.putString(it.name)
                buffer.putUShort(it.spriteId.value)
                buffer.putUInt(it.cooldown.value)
            }
        }
    }

    data class SpellUse(
        val sourcePosition: WorldPosition,
        val effects: Array<SpellEffect>
    ) : EgressDataPacket(SPELL_USE) {

        data class SpellEffect(
            val spriteId: SpriteId,
            val duration: Milliseconds
        )
        override fun packData(buffer: ByteBuffer) {
            buffer.putVector(sourcePosition)
            buffer.putArray(effects) {
                buffer.putUShort(it.spriteId.value)
                buffer.putUInt(it.duration.value)
            }
        }
    }

    data class DamageTaken(
        val damages: Array<Damage>
    ) : EgressDataPacket(DAMAGE_TAKEN) {
        data class Damage(
            val position: WorldPosition,
            val amount: UInt
        )

        override fun packData(buffer: ByteBuffer) {
            buffer.putArray(damages) {
                buffer.putVector(it.position)
                buffer.putUInt(it.amount)
            }
        }
    }
}
