package core.maps.entities.creatures

import core.maps.entities.items.Item
import core.types.CreatureName
import core.types.TileRadius
import core.types.WorldPosition
import pl.humberd.models.Experience

data class CreatureSeed(
    val name: CreatureName,
    val experience: Experience,
    val position: WorldPosition,
    val tilesViewRadius: TileRadius,
    val bodyRadius: Float,
    val equipment: Map<EquipmentSlotType, Item>,
    val backpack: Array<Item?>
)
