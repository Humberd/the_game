package core.maps.entities.creatures

import core.maps.entities.items.Item
import core.types.*

data class CreatureSeed(
    val name: CreatureName,
    val experience: Experience,
    val spriteId: SpriteId,
    val position: WorldPosition,
    val tilesViewRadius: TileRadius,
    val bodyRadius: Float,
    val equipment: Map<EquipmentSlotType, Item>
)
