package core.maps.entities.creatures

import core.maps.entities.items.CombatItem
import core.maps.entities.items.Item
import errors.NOT_REACHED

class CreatureEquipment(
    private val creature: Creature
) {
    val headSlot = EquipmentSlot()
    val bodySlot = EquipmentSlot()
    val legsSlot = EquipmentSlot()
    val feetSlot = EquipmentSlot()
    val leftHandSlot = EquipmentSlot()
    val rightHandSlot = EquipmentSlot()

    inner class EquipmentSlot {
        var item: Item? = null
            private set

        fun isEmpty() = item == null

        fun unequip() {
            val oldItem = item
            if (oldItem == null) {
                return
            }
            if (oldItem is CombatItem) {
                oldItem.modifiers.forEach { modifier ->
                    val item = modifier.modificationItem
                    if (item == null) {
                        return@forEach
                    }
                    item.statType.getStatFor(creature).removeModification(item)
                }
                creature.stats.recalculateCurrent()
            }
            item = null
            creature.hooks.onItemUnEquipped(oldItem)
        }

        fun equip(newItem: Item) {
            unequip()
            if (newItem is CombatItem) {
                newItem.modifiers.forEach { modifier ->
                    val item = modifier.modificationItem
                    if (item == null) {
                        return@forEach
                    }
                    item.statType.getStatFor(creature).addModification(item)
                }
                creature.stats.recalculateCurrent()
            }
            this.item = newItem
            creature.hooks.onItemEquipped(newItem)
        }
    }
}

enum class EquipmentSlotType(val value: Short) {
    NONE(0x00) {
        override fun getSlot(slot: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            NOT_REACHED()
        }
    },
    HEAD(0x01) {
        override fun getSlot(slot: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return slot.headSlot
        }
    },
    BODY(0x02) {
        override fun getSlot(slot: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return slot.bodySlot
        }
    },
    LEGS(0x04) {
        override fun getSlot(slot: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return slot.legsSlot
        }
    },
    FEET(0x08) {
        override fun getSlot(slot: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return slot.feetSlot
        }
    },
    LEFT_HAND(0x18) {
        override fun getSlot(slot: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return slot.leftHandSlot
        }
    },
    RIGHT_HAND(0x32) {
        override fun getSlot(slot: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return slot.rightHandSlot
        }
    };

    abstract fun getSlot(slot: CreatureEquipment): CreatureEquipment.EquipmentSlot
}
