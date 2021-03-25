package core.maps.entities.creatures

import errors.NOT_REACHED

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
