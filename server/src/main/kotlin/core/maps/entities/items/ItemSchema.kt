package core.maps.entities.items

import core.types.ItemSchemaId
import core.types.ResourceId
import infrastructure.database.types.Equippable

data class ItemSchema(
    val id: ItemSchemaId,
    val equippable: Equippable,
    val stackable: Boolean
)
