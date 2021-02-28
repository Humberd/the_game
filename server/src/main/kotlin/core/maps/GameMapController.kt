package core.maps

import core.PlayerCharacter
import core.types.PID
import org.mini2Dx.gdx.math.Vector2

class GameMapController(
    private val map: GameMap
) {
    private val players = HashMap<PID, PlayerCharacter>()

    fun addPlayer(playerCharacter: PlayerCharacter) {
        players[playerCharacter.id] = playerCharacter
    }

    fun removePlayer(pid: PID) {
        players.remove(pid)
    }

    fun movePlayerBy(pid: PID, vector: Vector2) {
        val player = getPlayer(pid)
        player.position.mulAdd(vector, player.movementSpeed)
    }

    private fun getPlayer(pid: PID): PlayerCharacter {
        return players[pid] ?: throw Error("PlayerController not found for ${pid}")
    }
}
