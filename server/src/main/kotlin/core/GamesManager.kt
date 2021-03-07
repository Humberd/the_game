package core

import core.maps.GameMapController
import core.maps.GameMapGenerator
import core.maps.entities.Player
import core.types.GameMapId
import core.types.IID
import core.types.PID
import core.types.WorldPosition
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

    fun addPlayer(player: Player, gameMapId: GameMapId) {
        val ctrl = getMapController(gameMapId)
        playerLUT[player.pid] = gameMapId
        ctrl.addPlayer(player)
    }

    fun removePlayer(pid: PID) {
        val ctrl = getMapController(pid)
        playerLUT.remove(pid)
        ctrl.removePlayer(pid)
    }

    fun movePlayerBy(pid: PID, vector: Vector2) {
        val ctrl = getMapController(pid)
        ctrl.moveBy(pid, vector)
    }

    fun dragItemOnTerrain(pid: PID, iid: IID, targetPosition: WorldPosition) {
        val ctrl = getMapController(pid)
        ctrl.moveItemOnTerrain(pid, iid, targetPosition)
    }

    private fun getMapController(pid: PID): GameMapController {
        return controllers[playerLUT[pid]] ?: throw Error("GameMapController not found for ${pid}")
    }

    private fun getMapController(gameMapId: GameMapId): GameMapController {
        return controllers[gameMapId] ?: throw Error("GameMapController not found for ${gameMapId}")
    }
}
