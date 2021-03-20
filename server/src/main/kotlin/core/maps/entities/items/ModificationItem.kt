package core.maps.entities.items

import core.maps.entities.creatures.StatType

class ModificationItem(
    itemSchema: ItemSchema,
    val statType: StatType,
    val value: Int,
    val modificationType: ModificationType
) : Item(itemSchema) {

}
