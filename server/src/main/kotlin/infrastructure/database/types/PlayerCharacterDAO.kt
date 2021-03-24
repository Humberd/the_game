package infrastructure.database.types

import core.maps.entities.creatures.CreatureSeed
import core.maps.entities.creatures.EquipmentSlotType
import core.maps.entities.creatures.StatType
import core.maps.entities.creatures.player.PlayerSeed
import core.maps.entities.creatures.player.SpellsContainer
import core.maps.entities.items.*
import core.types.*

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
                itemSchema = ItemSchema(
                    id = 123,
                    name = "Rotter Shield",
                    resourceId = ResourceId(1u),
                    equippable = Equippable.within(EquipmentSlotType.HEAD),
                    isStackable = false
                ),
                modifiers = listOf(
                    ModificationSlot(
                        modificationItem= ModificationItem(
                            itemSchema = ItemSchema(
                                id = 321,
                                name = "Health Boost",
                                resourceId = ResourceId(2u),
                                equippable = Equippable.DONT(),
                                isStackable = false
                            ),
                            statType = StatType.HEALTH_POOL,
                            value = 30,
                            modificationType = ModificationType.FLAT
                        ),
                        isLocked = false
                    )
                )
            )
        )
    )

    fun toPlayerSeed() = PlayerSeed(
        pid = pid,
        spellsContainer = SpellsContainer()
    )
}
