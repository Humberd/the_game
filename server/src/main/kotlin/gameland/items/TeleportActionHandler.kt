package gameland.items

import core.maps.GameMapController
import core.maps.entities.Creature
import core.maps.entities.Item
import gameland.ItemActionHandler

object TeleportActionHandler : ItemActionHandler {
    override fun onItemWalkedOn(context: GameMapController, creature: Creature, item: Item) {
        context.moveTo(creature, item.position.cpy().sub(200f, 0f))
    }
}
