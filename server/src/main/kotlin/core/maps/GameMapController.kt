package core.maps

import core.PlayerCharacter
import core.StateChangeNotifier
import core.types.InstanceId
import core.types.PID
import core.types.WorldPosition
import org.mini2Dx.gdx.math.Vector2

class GameMapController(
    private val notifier: StateChangeNotifier,
    private val map: GameMap
) {
    private val players = HashMap<PID, PlayerCharacter>()
    private val items = HashMap<InstanceId, GameMap.Item>()

    init {
        map.items.forEach { items[it.instanceId] = it }
    }

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

    fun dragItemOnTerrain(pid: PID, itemInstanceId: InstanceId, targetPosition: WorldPosition) {
        val item = getItem(itemInstanceId)
        val currentItemCoords = GameMap.toGridPosition(item.position)
        val currentItemTile = map.getTileAt(currentItemCoords)

        val targetItemCoords = GameMap.toGridPosition(targetPosition)
        val targetItemTile = map.getTileAt(targetItemCoords)

        currentItemTile.moveItemToTile(item, targetItemTile)
        item.position = targetPosition

        notifyEveryone { otherPID -> notifier.notifyTerrainItemsUpdate(getPlayer(otherPID)) }
    }

    private fun getPlayer(pid: PID): PlayerCharacter {
        return players[pid] ?: throw Error("PlayerController not found for ${pid}")
    }

    private fun getItem(instanceId: InstanceId): GameMap.Item {
        return items[instanceId] ?: throw Error("GameMap.Item not found for ${instanceId}")
    }

    private fun getPlayersOtherThan(pid: PID): List<PlayerCharacter> {
        return players.values.filter { it.id != pid }
    }

    fun notifyEveryone(callback: (pid: PID) -> Unit) {
        players.keys.forEach { pid -> callback.invoke(pid) }
    }
}
