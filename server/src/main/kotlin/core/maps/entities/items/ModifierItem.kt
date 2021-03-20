package core.maps.entities.items

class ModificationSlot(
    val modifierItem: ModifierItem?,
    val isLocked: Boolean
)

class ModifierItem(
    itemSchema: ItemSchema,
    val attribute: String,
    val value: Int,
    val modificationType: String
) : Item(itemSchema)
