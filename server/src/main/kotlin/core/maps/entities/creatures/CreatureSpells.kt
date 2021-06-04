package core.maps.entities.creatures

import core.maps.entities.Lifecycle
import core.maps.entities.spells.SpellHandler
import core.maps.entities.spells.handlers.SpearThrowHandler
import core.types.WorldPosition

class CreatureSpells(private val thisCreature: Creature) : Lifecycle {
    private val slots = arrayListOf<SpellHandler?>()

    override fun onInit() {
        slots.add(SpearThrowHandler(thisCreature, thisCreature.context))
    }

    fun spellCastStart(spellSlot: UByte, targetPosition: WorldPosition) {
        val slotIndex = spellSlot.toInt()
        require(slots.size > slotIndex)

        val spellHandler = slots[slotIndex]
        require(spellHandler != null)
        spellHandler.onCastStart(targetPosition)
    }

    fun spellCastEnd(spellSlot: UByte, targetPosition: WorldPosition) {
        val slotIndex = spellSlot.toInt()
        require(slots.size > slotIndex)

        val spellHandler = slots[slotIndex]
        require(spellHandler != null)
        spellHandler.onCastEnd(targetPosition)
    }
}
