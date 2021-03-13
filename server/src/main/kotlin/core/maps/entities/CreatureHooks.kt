package core.maps.entities

interface CreatureHooks {
    fun onAddedToMap(gameMap: GameMap)
    fun onRemovedFromMap(gameMap: GameMap)
    fun onMoved()
    fun onOtherCreatureAppearInViewRange(otherCreature: Creature)
    fun onOtherCreatureDisappearFromViewRange(otherCreature: Creature) {}
    fun onOtherCreaturePositionChange(otherCreature: Creature) {}
}
