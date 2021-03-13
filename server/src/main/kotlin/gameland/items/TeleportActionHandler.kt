package gameland.items

import core.maps.entities.Creature
import core.maps.entities.Item
import gameland.ItemActionHandler

object TeleportActionHandler : ItemActionHandler {
    override fun onItemWalkedOn(creature: Creature, item: Item) {
//        creature.moveTo(item.position.cpy().sub(200f, 0f))
    }
}
