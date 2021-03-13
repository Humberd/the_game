package gameland

import core.maps.entities.Creature
import core.maps.entities.Item

private class DefaultItemActionHandlerImpl : ItemActionHandler

interface ItemActionHandler {
    companion object {
        val defaultImpl: ItemActionHandler = DefaultItemActionHandlerImpl()
    }

    fun onItemWalkedOn(creature: Creature, item: Item) {}
    fun onItem__(value: Int) = value
}
