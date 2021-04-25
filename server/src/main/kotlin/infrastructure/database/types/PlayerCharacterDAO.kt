package infrastructure.database.types

import core.maps.ItemSchemaStore
import core.maps.entities.creatures.CreatureSeed
import core.maps.entities.creatures.EquipmentSlotType
import core.maps.entities.creatures.StatType
import core.maps.entities.creatures.player.PlayerSeed
import core.maps.entities.creatures.player.SpellsContainer
import core.maps.entities.items.*
import core.types.*
import pl.humberd.udp.models.PID

data class PlayerCharacterDAO(
    val pid: PID,
    val name: CreatureName,
    val experience: Experience,
    val spriteId: SpriteId,
    val position: WorldPosition,
    val tilesViewRadius: TileRadius,
    val bodyRadius: Float
) {
    fun toCreatureSeed() = CreatureSeed(
        name,
        experience,
        spriteId,
        position,
        tilesViewRadius,
        bodyRadius,
        equipment = mapOf(
            EquipmentSlotType.HEAD to CombatItem(
                itemSchema = ItemSchemaStore.get(ItemSchemaId(1u)),
                modifiers = listOf(
                    ModificationSlot(
                        modificationItem= ModificationItem(
                            itemSchema = ItemSchemaStore.get(ItemSchemaId(2u)),
                            statType = StatType.HEALTH_POOL,
                            value = 30,
                            modificationType = ModificationType.FLAT
                        ),
                        isLocked = false
                    )
                )
            )
        ),
        backpack = arrayOf(
            PrimitiveItem(
                itemSchema = ItemSchemaStore.get(ItemSchemaId(3u)),
                stackCount = 2u
            ),
            CombatItem(
                itemSchema = ItemSchemaStore.get(ItemSchemaId(4u)),
                modifiers = listOf(
                    ModificationSlot(
                        modificationItem= ModificationItem(
                            itemSchema = ItemSchemaStore.get(ItemSchemaId(5u)),
                            statType = StatType.ATTACK,
                            value = 10,
                            modificationType = ModificationType.BASE_PERCENTAGE
                        ),
                        isLocked = false
                    )
                )
            ),
            PrimitiveItem(
                itemSchema = ItemSchemaStore.get(ItemSchemaId(3u)),
                stackCount = 1u
            ),
        )
    )

    fun toPlayerSeed() = PlayerSeed(
        pid = pid,
        spellsContainer = SpellsContainer()
    )
}
