package core.maps.entities.creatures

import core.maps.entities.items.CombatItem
import core.maps.entities.items.Item

class CreatureEquipment(
    private val creature: Creature
) {
    val headSlot = EquipmentSlot()
    val bodySlot = EquipmentSlot()
    val legsSlot = EquipmentSlot()
    val feetSlot = EquipmentSlot()
    val leftHandSlot = EquipmentSlot()
    val rightHandSlot = EquipmentSlot()

    fun onInit(equipment: Map<EquipmentSlotType, Item>) {
        equipment.forEach { type, item ->
            type.getSlot(this).equip(item)
        }
    }

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
//            creature.hooks.onItemUnEquipped(oldItem)
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
//            creature.hooks.onItemEquipped(newItem)
        }
    }
}

