package gameland.items

import core.maps.GameMapController
import core.maps.entities.Item
import core.maps.entities.Player
import gameland.ItemActionHandler

object TeleportActionHandler : ItemActionHandler {
    override fun onItemWalkedOn(context: GameMapController, player: Player, item: Item) {
        context.movePlayerTo(player, item.position.cpy().sub(200f, 0f))
    }
}
