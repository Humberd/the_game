package infrastructure.udp.egress

import core.maps.entities.creatures.StatValue
import core.types.*
import infrastructure.udp.egress.EgressPacketType.*
import pl.humberd.udp.models.CID
import pl.humberd.udp.models.PID
import pl.humberd.udp.models.SID
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
    DAMAGE_TAKEN(0x28),
    PROJECTILE_SEND(0x29),
    EQUIPMENT_UPDATE(0x2A),
    CREATURE_STATS_UPDATE(0x2B),
    BACKPACK_UPDATE(0x2C),
    PING_RESPONSE(0x2D),
    TERRAIN_WALLS_UPDATE(0x2E)
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
        val baseHealth: Int,
        val currentHealth: Int,
        val position: WorldPosition,
        val spriteId: SpriteId,
        val bodyRadius: Float,
        val attackTriggerRadius: Float,
        val isBeingAttackedByMe: Boolean
    ) : EgressDataPacket(CREATURE_UPDATE) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(cid.value)
            buffer.putString(name.value)
            buffer.putInt(baseHealth)
            buffer.putInt(currentHealth)
            buffer.putVector(position)
            buffer.putUShort(spriteId.value)
            buffer.putFloat(bodyRadius)
            buffer.putFloat(attackTriggerRadius)
            buffer.putUByte(if (isBeingAttackedByMe) 1u else 0u)
        }
    }

    data class CreatureDisappear(
        val cid: CID
    ) : EgressDataPacket(CREATURE_DISAPPEAR) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUInt(cid.value)
        }
    }

    @Spammable
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
            val itemInstanceId: ItemInstanceId,
            val type: Any,
            val position: WorldPosition
        )

        override fun packData(buffer: ByteBuffer) {
            buffer.putList(items) {
                buffer.putUInt(it.itemInstanceId.value)
//                buffer.putUInt(it.type.id.toUInt())
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

    data class ProjectileSend(
        val spriteId: SpriteId,
        val sourcePosition: WorldPosition,
        val targetPosition: WorldPosition,
        val duration: Milliseconds
    ) : EgressDataPacket(PROJECTILE_SEND) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putUShort(spriteId.value)
            buffer.putVector(sourcePosition)
            buffer.putVector(targetPosition)
            buffer.putUInt(duration.value)
        }
    }

    data class EquipmentUpdate(
        val headSlot: EquipmentSlotDTO,
        val bodySlot: EquipmentSlotDTO,
        val legsSlot: EquipmentSlotDTO,
        val feetSlot: EquipmentSlotDTO,
        val leftHandSlot: EquipmentSlotDTO,
        val rightHandSlot: EquipmentSlotDTO,
    ) : EgressDataPacket(EQUIPMENT_UPDATE) {
        data class EquipmentSlotDTO(
            private val itemInstanceId: ItemInstanceId
        )

        override fun packData(buffer: ByteBuffer) {

        }
    }

    data class CreatureStatsUpdate(
        val defense: CreatureStatPacket,
        val attack: CreatureStatPacket,
        val attackSpeed: CreatureStatPacket,
        val movementSpeed: CreatureStatPacket,
        val healthPool: CreatureStatPacket,
    ) : EgressDataPacket(CREATURE_STATS_UPDATE) {
        data class CreatureStatPacket(
            val base: Number,
            val current: Number
        ) {
            companion object {
                fun <T : Number> from(value: StatValue<T>): CreatureStatPacket {
                    return CreatureStatPacket(
                        base = value.base,
                        current = value.current
                    )
                }
            }
        }

        override fun packData(buffer: ByteBuffer) {
            packStat(buffer, defense)
            packStat(buffer, attack)
            packStat(buffer, attackSpeed)
            packStat(buffer, movementSpeed)
            packStat(buffer, healthPool)
        }

        private fun packStat(buffer: ByteBuffer, stat: CreatureStatPacket) {
            packNumber(buffer, stat.base)
            packNumber(buffer, stat.current)
        }

        private fun packNumber(buffer: ByteBuffer, value: Number) {
            when (value) {
                is Int -> buffer.putInt(value)
                is Float -> buffer.putFloat(value)
                else -> throw Error("Unsupported Stat Type")
            }
        }
    }

    data class BackpackUpdate(
        val items: Array<BackpackSlotDTO?>
    ) : EgressDataPacket(BACKPACK_UPDATE) {
        data class BackpackSlotDTO(
            val itemSchemaId: ItemSchemaId,
            val stackCount: UShort
        )

        override fun packData(buffer: ByteBuffer) {
            buffer.putNullableArray(items) {
                buffer.putUShort(it.itemSchemaId.value)
                buffer.putUShort(it.stackCount)
            }
        }
    }

    @Spammable
    class PingResponse : EgressDataPacket(PING_RESPONSE) {
        override fun packData(buffer: ByteBuffer) {
            // nothing to send
        }

        override fun toString() = "PingResponse()"
    }

    data class TerrainWallsUpdate(
        val chains: Array<Array<WorldPosition>>
    ) : EgressDataPacket(TERRAIN_WALLS_UPDATE) {
        override fun packData(buffer: ByteBuffer) {
            buffer.putArray(chains) {
                buffer.putArray(it) {
                    buffer.putFloat(it.x)
                    buffer.putFloat(it.y)
                }
            }
        }
    }
}
