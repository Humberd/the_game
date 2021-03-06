package gameland

import core.maps.GameMapController
import core.maps.entities.Item
import core.maps.entities.Player

interface ItemActionHandler {
    fun onItemWalkedOn(context: GameMapController, player: Player, item: Item) {}
    fun onItem__(){}
}
