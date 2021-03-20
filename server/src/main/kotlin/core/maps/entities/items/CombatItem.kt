package core.maps.entities.items

class CombatItem(
    itemSchema: ItemSchema,
    val modifiers: List<ModificationSlot>
) : Item(itemSchema)
