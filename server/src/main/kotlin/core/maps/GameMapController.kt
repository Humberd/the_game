package core.maps

import core.PlayerCharacter
import core.StateChangeNotifier
import core.types.PID
import org.mini2Dx.gdx.math.Vector2

class GameMapController(
    private val notifier: StateChangeNotifier,
    private val map: GameMap
) {
    private val players = HashMap<PID, PlayerCharacter>()

    fun addPlayer(player: PlayerCharacter) {
        players[player.id] = player

        notifyEveryone { notifier.notifyPlayerUpdate(it, player) }
        getPlayersOtherThan(player.id).forEach {
            notifier.notifyPlayerUpdate(player.id, it)
        }

        notifier.notifyTerrainUpdate(player, map)
    }

    fun removePlayer(pid: PID) {
        players.remove(pid)

        notifyEveryone { notifier.notifyPlayerDisconnect(it, pid) }
    }

    fun movePlayerBy(pid: PID, vector: Vector2) {
        val player = getPlayer(pid)
        player.position.mulAdd(vector, player.movementSpeed)

        notifyEveryone { notifier.notifyPlayerPositionUpdate(it, player) }
        notifier.notifyTerrainUpdate(player, map)
    }

    private fun getPlayer(pid: PID): PlayerCharacter {
        return players[pid] ?: throw Error("PlayerController not found for ${pid}")
    }

    private fun getPlayersOtherThan(pid: PID): List<PlayerCharacter> {
        return players.values.filter { it.id != pid }
    }

    fun notifyEveryone(callback: (pid: PID) ->  Unit) {
        players.keys.forEach { pid -> callback.invoke(pid) }
    }
}
