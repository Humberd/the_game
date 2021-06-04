package core.maps.entities.creatures

import core.maps.entities.items.Item

interface CreatureHooks {
    fun onAddedToMap() {}
    fun onRemovedFromMap() {}
    fun onMoved(tileChanged: Boolean) {}
    fun onOtherCreatureAppearInViewRange(otherCreature: Creature) {}
    fun onOtherCreatureDisappearFromViewRange(otherCreature: Creature) {}
    fun onOtherCreaturePositionChange(otherCreature: Creature) {}

    fun onStartAttackOtherCreature(otherCreature: Creature) {}
    fun onBeingAttackedBy(otherCreature: Creature) {}

    fun onStoppedAttackOtherCreature(otherCreature: Creature) {}
    fun onStoppedBeingAttackedBy(otherCreature: Creature) {}

    fun onSelfDamageTaken(damage: UInt) {}
    fun onOtherCreatureDamageTaken(otherCreature: Creature, damage: UInt) {}
    fun onDeath() {}

    fun onItemEquipped(item: Item) {}
    fun onItemUnEquipped(item: Item) {}
}
