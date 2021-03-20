package core.maps.entities.items

import core.types.ResourceId
import infrastructure.database.types.Equippable

data class ItemSchema(
    val id: Int,
    val name: String,
    val resourceId: ResourceId,
    val equippable: Equippable,
    val isStackable: Boolean
)

val MAX_STACK_COUNT = 100u

abstract class Item(
    val itemSchema: ItemSchema,
    stackCount: UShort = 1u
) {
    var stackCount: UShort = stackCount
        private set(value) {
            require(value > 0u && value <= MAX_STACK_COUNT)
            if (!itemSchema.isStackable) require(value == (1).toUShort())
            field = value
        }
}

class PrimitiveItem(itemSchema: ItemSchema) : Item(itemSchema)

class CombatItem(
    itemSchema: ItemSchema,
    val modifiers: List<ModificationSlot>
) : Item(itemSchema)

