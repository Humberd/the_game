package core.maps.entities.creatures

import errors.NOT_REACHED

enum class EquipmentSlotType(val value: Short) {
    NONE(0x00) {
        override fun getSlot(creatureEquipment: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            NOT_REACHED()
        }
    },
    HEAD(0x01) {
        override fun getSlot(creatureEquipment: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return creatureEquipment.headSlot
        }
    },
    BODY(0x02) {
        override fun getSlot(creatureEquipment: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return creatureEquipment.bodySlot
        }
    },
    LEGS(0x04) {
        override fun getSlot(creatureEquipment: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return creatureEquipment.legsSlot
        }
    },
    FEET(0x08) {
        override fun getSlot(creatureEquipment: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return creatureEquipment.feetSlot
        }
    },
    LEFT_HAND(0x10) {
        override fun getSlot(creatureEquipment: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return creatureEquipment.leftHandSlot
        }
    },
    RIGHT_HAND(0x20) {
        override fun getSlot(creatureEquipment: CreatureEquipment): CreatureEquipment.EquipmentSlot {
            return creatureEquipment.rightHandSlot
        }
    };

    abstract fun getSlot(creatureEquipment: CreatureEquipment): CreatureEquipment.EquipmentSlot
}
