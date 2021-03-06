package core.maps.entities.creatures

import core.maps.entities.GameMap
import core.maps.entities.items.Item
import core.maps.obstacles.Obstacle
import core.maps.shapes.Wall

interface CreatureHooks {
    fun onAddedToMap(gameMap: GameMap) {}
    fun onRemovedFromMap(gameMap: GameMap) {}
    fun onMoved(tileChanged: Boolean) {}
    fun onCollideWith(wall: Wall) {}
    fun onCollideWith(wall: Obstacle) {}
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
