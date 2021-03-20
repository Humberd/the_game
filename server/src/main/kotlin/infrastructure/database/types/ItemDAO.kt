package infrastructure.database.types

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
    enum class Slot(val value: Short) {
        NONE(0x00),
        HELMET(0x01),
        ARMOR(0x02),
        LEGS(0x04),
        BOOTS(0x08),
        LEFT_HAND(0x16),
        RIGHT_HAND(0x32),
    }

    companion object {
        fun DONT() = within(Slot.NONE)
        fun within(vararg slot: Slot): Equippable {
            var mask: Short = 0
            slot.forEach { mask = mask or it.value }
            return Equippable(mask)
        }
    }
}
