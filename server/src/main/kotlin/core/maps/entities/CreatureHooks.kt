package core.maps.entities

import core.maps.shapes.Wall

interface CreatureHooks {
    fun onAddedToMap(gameMap: GameMap)
    fun onRemovedFromMap(gameMap: GameMap)
    fun onMoved()
    fun onCollideWith(wall: Wall) {}
    fun onOtherCreatureAppearInViewRange(otherCreature: Creature)
    fun onOtherCreatureDisappearFromViewRange(otherCreature: Creature) {}
    fun onOtherCreaturePositionChange(otherCreature: Creature) {}
}
