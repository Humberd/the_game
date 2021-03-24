package infrastructure.database.types

import core.maps.entities.creatures.EquipmentSlotType
import core.types.IID
import core.types.ResourceId
import kotlin.experimental.or

data class ItemDAO(
    val iid: IID,
    val name: String,
    val resourceId: ResourceId,
    val equippable: Equippable,
    val modifiers: Collection<ModifierDAO>
)

data class ModifierDAO(
    val attribute: String,
    val value: Int,
    val type: String
)

inline class Equippable(val mask: Short) {
    companion object {
        fun DONT() = within(EquipmentSlotType.NONE)
        fun within(vararg slot: EquipmentSlotType): Equippable {
            var mask: Short = 0
            slot.forEach { mask = mask or it.value }
            return Equippable(mask)
        }
    }
}
