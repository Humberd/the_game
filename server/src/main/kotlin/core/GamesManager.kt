package core

import core.maps.GameMapController
import core.maps.GameMapGenerator
import core.types.GameMapId
import core.types.PID
import org.mini2Dx.gdx.math.Vector2

class GamesManager(
    private val notifier: StateChangeNotifier,
) {
    private val controllers = HashMap<GameMapId, GameMapController>()
    private val playerLUT = HashMap<PID, GameMapId>()

    init {
        GameMapGenerator.generateMap1(20, 20).also { map ->
            controllers[map.id] = GameMapController(notifier, map)
        }
    }

    fun addPlayer(playerCharacter: PlayerCharacter, gameMapId: GameMapId) {
        val ctrl = getMapController(gameMapId)
        playerLUT[playerCharacter.id] = gameMapId
        ctrl.addPlayer(playerCharacter)
    }

    fun removePlayer(pid: PID) {
        val ctrl = getMapController(pid)
        playerLUT.remove(pid)
        ctrl.removePlayer(pid)
    }

    fun movePlayerBy(pid: PID, vector: Vector2) {
        val ctrl = getMapController(pid)
        ctrl.movePlayerBy(pid, vector)
    }

    private fun getMapController(pid: PID): GameMapController {
        return controllers[playerLUT[pid]] ?: throw Error("GameMapController not found for ${pid}")
    }

    private fun getMapController(gameMapId: GameMapId): GameMapController {
        return controllers[gameMapId] ?: throw Error("GameMapController not found for ${gameMapId}")
    }
}
