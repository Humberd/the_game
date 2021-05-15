package core

import core.maps.entities.GameContextImpl
import core.maps.entities.GameMapSeed
import core.maps.entities.creatures.player.PlayerSeed
import core.types.GameId
import core.types.WorldPosition
import mu.KLogging
import pl.humberd.models.CID
import pl.humberd.models.PID
import pl.humberd.models.SID

class GamesManager(
    private val playerNotifier: PlayerNotifier
) {
    companion object : KLogging()

    private val contexts = HashMap<GameId, GameContextImpl>()
    private val playerLUT = HashMap<PID, GameId>()

    fun newGame(gameMapSeed: GameMapSeed) {
        val context = GameContextImpl(
            playerNotifier,
            gameMapSeed,
            GameId(1u)
        )

        context.onInit()

        this.contexts[context.gameId] = context
    }

    fun onPhysicsStep(deltaTime: Float) {
        contexts.values.forEach {
            it.onUpdate(deltaTime)
        }
    }

    fun addPlayer(playerSeed: PlayerSeed) {
        //fixme: hardcoded gameMapId
        val gameId = GameId(1u)
        val context = getContext(gameId)

        if (playerLUT[playerSeed.pid] != null) {
            removePlayer(playerSeed.pid)
        }

        playerLUT[playerSeed.pid] = gameId
        val player = context.create(playerSeed)
        logger.info { "Created new player with ${player.cid}" }
    }

    fun removePlayer(pid: PID) {
        val map = getContext(pid)
        playerLUT.remove(pid)
        map.destroy(pid)
    }

    fun movePlayerTo(pid: PID, targetPosition: WorldPosition) {
        getContext(pid).creatures.moveTo(pid, targetPosition)
    }

    fun useSpell(pid: PID, sid: SID) {
    }

    fun startBasicAttacking(pid: PID, targetCid: CID) {
//        getContext(pid).startAttacking(pid, targetCid)
    }

    fun stopBasicAttacking(pid: PID) {
//        getContext(pid).stopAttacking(pid)
    }

    fun requestPlayerStatsUpdate(pid: PID) {
//        val player = getContext(pid).get(pid)
//        notifier.notifyPlayerStats(player)
//        notifier.notifyBackpackUpdate(player)
    }

    fun ping(pid: PID) {
//        notifier.notifyPingResponse(pid)
    }


    //region Utilities
    private fun getContext(pid: PID): GameContextImpl {
        return contexts[playerLUT[pid]] ?: throw Error("GameMap not found for ${pid}")
    }

    private fun getContext(gameId: GameId): GameContextImpl {
        return contexts[gameId] ?: throw Error("GameMap not found for ${gameId}")
    }
    //endregion
}
