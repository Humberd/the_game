package core.maps

import core.PlayerCharacter
import core.StateChangeNotifier
import core.types.PID
import core.types.WorldPosition
import org.mini2Dx.gdx.math.Vector2

class GameMapController(
    private val notifier: StateChangeNotifier,
    private val map: GameMap
) {
    private val players = HashMap<PID, PlayerCharacter>()

    fun addPlayer(player: PlayerCharacter) {
        players[player.id] = player

        getPlayersOtherThan(player.id).forEach { otherPlayer ->
            notifier.notifyPlayerUpdate(player.id, otherPlayer)
        }
        notifyEveryone { otherPID -> notifier.notifyPlayerUpdate(otherPID, player) }

        movePlayerTo(player, Vector2(400f, 400f))
    }

    fun removePlayer(pid: PID) {
        players.remove(pid)

        notifyEveryone { notifier.notifyPlayerDisconnect(it, pid) }
    }

    fun movePlayerBy(pid: PID, vector: Vector2) {
        val player = getPlayer(pid)

        val newPosition = player.position.cpy().mulAdd(vector, player.movementSpeed)
        movePlayerTo(player, newPosition)
    }

    fun movePlayerTo(player: PlayerCharacter, vector: WorldPosition) {
        val lastGridPosition = player.lastUpdate.gridPosition

        player.position.set(vector)
        player.lastUpdate.gridPosition = GameMap.toGridPosition(player.position)
        val currentGridPosition = player.lastUpdate.gridPosition

        if (lastGridPosition != currentGridPosition) {
            player.lastUpdate.tileSlice = map.getTilesAround(currentGridPosition, player.viewRadius.toInt())
            notifier.notifyTerrainUpdate(player)
            notifier.notifyTerrainItemsUpdate(player)
        }

        notifyEveryone { otherPID -> notifier.notifyPlayerPositionUpdate(otherPID, player) }
    }

    private fun getPlayer(pid: PID): PlayerCharacter {
        return players[pid] ?: throw Error("PlayerController not found for ${pid}")
    }

    private fun getPlayersOtherThan(pid: PID): List<PlayerCharacter> {
        return players.values.filter { it.id != pid }
    }

    fun notifyEveryone(callback: (pid: PID) -> Unit) {
        players.keys.forEach { pid -> callback.invoke(pid) }
    }
}
