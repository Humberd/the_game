package gameland

import core.PlayerCharacter
import core.maps.GameMap
import core.maps.GameMapController

interface ItemActionHandler {
    fun onItemWalkedOn(context: GameMapController, player: PlayerCharacter, item: GameMap.Item) {}
    fun onItem__(){}
}
