package gameland

import core.maps.entities.GameMapObject
import core.maps.entities.creatures.Creature

private class DefaultItemActionHandlerImpl : ItemActionHandler

interface ItemActionHandler {
    companion object {
        val defaultImpl: ItemActionHandler = DefaultItemActionHandlerImpl()
    }

    fun onItemWalkedOn(creature: Creature, gameMapObject: GameMapObject) {}
    fun onItem__(value: Int) = value
}
