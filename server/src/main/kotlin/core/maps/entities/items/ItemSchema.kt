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
