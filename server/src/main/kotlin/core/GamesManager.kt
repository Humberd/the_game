package core

import core.maps.GameMapGenerator
import core.maps.entities.GameMap
import core.maps.entities.creatures.CreatureSeed
import core.maps.entities.creatures.player.Player
import core.maps.entities.creatures.player.PlayerSeed
import core.types.GameMapId
import core.types.WorldPosition
import pl.humberd.models.CID
import pl.humberd.models.PID
import pl.humberd.models.SID

class GamesManager(
    private val notifier: StateChangeNotifier,
) {
    private val maps = HashMap<GameMapId, GameMap>()
    private val playerLUT = HashMap<PID, GameMapId>()

    init {
        GameMapGenerator.generateMap1(20, 20, notifier).also { map ->
            maps[map.id] = map
        }
    }

    fun addPlayer(creatureSeed: CreatureSeed, playerSeed: PlayerSeed) {
        //fixme: hardcoded gameMapId
        val gameMapId = GameMapId(1u)
        val map = getMap(gameMapId)
        playerLUT[playerSeed.pid] = gameMapId

        val player = Player(creatureSeed, map, notifier, playerSeed)
        map.creatures.add(player)
    }

    fun removePlayer(pid: PID) {
        val map = getMap(pid)
        playerLUT.remove(pid)
        map.creatures.remove(pid)
    }

    fun movePlayerTo(pid: PID, targetPosition: WorldPosition) {
        getMap(pid).creatures.moveTo(pid, targetPosition)
    }

    fun useSpell(pid: PID, sid: SID) {
        val map = getMap(pid)
//        map.useSpell(pid, sid)
    }

    fun onPhysicsStep(deltaTime: Float) {
        maps.values.forEach {
            it.onPhysicsStep(deltaTime)
        }
    }

    fun startBasicAttacking(pid: PID, targetCid: CID) {
        getMap(pid).startAttacking(pid, targetCid)
    }

    fun stopBasicAttacking(pid: PID) {
        getMap(pid).stopAttacking(pid)
    }

    fun requestPlayerStatsUpdate(pid: PID) {
        val player = getMap(pid).creatures.get(pid)
        notifier.notifyPlayerStats(player)
        notifier.notifyBackpackUpdate(player)
    }

    fun ping(pid: PID) {
        notifier.notifyPingResponse(pid)
    }


    //region Utilities
    private fun getMap(pid: PID): GameMap {
        return maps[playerLUT[pid]] ?: throw Error("GameMap not found for ${pid}")
    }

    private fun getMap(gameMapId: GameMapId): GameMap {
        return maps[gameMapId] ?: throw Error("GameMap not found for ${gameMapId}")
    }
    //endregion
}
