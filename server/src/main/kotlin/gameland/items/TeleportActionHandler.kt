package gameland.items

import core.PlayerCharacter
import core.maps.GameMap
import core.maps.GameMapController
import gameland.ItemActionHandler

object TeleportActionHandler : ItemActionHandler {
    override fun onItemWalkedOn(context: GameMapController, player: PlayerCharacter, item: GameMap.Item) {
        context.movePlayerTo(player, item.position.cpy().sub(200f, 0f))
    }
}
