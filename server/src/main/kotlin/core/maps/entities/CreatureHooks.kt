package core.maps.entities

import core.maps.shapes.Wall

interface CreatureHooks {
    fun onAddedToMap(gameMap: GameMap) {}
    fun onRemovedFromMap(gameMap: GameMap) {}
    fun onMoved(tileChanged: Boolean) {}
    fun onCollideWith(wall: Wall) {}
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
}
