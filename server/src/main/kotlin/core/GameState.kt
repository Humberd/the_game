package core

import core.types.ActionResult
import core.types.PID
import org.mini2Dx.gdx.math.Vector2

class GameState(
    private val notifier: StateChangeNotifier
) {
    private val players = HashMap<PID, PlayerCharacter>()

    fun addPlayer(playerCharacter: PlayerCharacter) {
        players.put(playerCharacter.id, playerCharacter)
        notifyEveryone {
            notifier.notifyPlayerUpdate(it, playerCharacter)
        }
    }

    fun removePlayer(id: PID) {
        players.remove(id)
        notifyEveryone {
            notifier.notifyPlayerDisconnect(it, id)
        }
    }

    fun movePlayerBy(id: PID, vector: Vector2): ActionResult {
        val player = players[id]
        if (player == null) {
            return ActionResult.ERROR
        }
        player.position.mulAdd(vector, player.movementSpeed)

        notifyEveryone {
            notifier.notifyPlayerPositionUpdate(it, player)
        }

        return ActionResult.OK
    }

    private inline fun notifyEveryone(callback: (PID) -> Unit) {
        players.values.forEach {
            callback.invoke(it.id)
        }
    }
}
