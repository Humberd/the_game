package core.maps.entities

import core.StateChangeNotifier

class PlayerHooks(
    private val player: Player,
    private val notifier: StateChangeNotifier
) {
    fun onAddedToMap(gameMap: GameMap) {
        notifier.notifyPlayerDetails(player.pid, player)
        notifier.notifyCreatureUpdate(player.pid, player)
        notifier.notifyTerrainUpdate(player)
    }

    fun onRemovedFromMap(gameMap: GameMap) {
        notifier.notifyCreatureDisappear(player.pid, player)
    }

    fun onMoved() {
        notifier.notifyCreatureUpdate(player.pid, player)
        notifier.notifyTerrainUpdate(player)
    }
}
