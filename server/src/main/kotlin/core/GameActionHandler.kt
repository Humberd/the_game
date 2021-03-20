package core

import infrastructure.database.Database
import infrastructure.udp.ingress.IngressPacket
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class GameActionHandler(
    private val gamesManager: GamesManager,
    private val database: Database
) {
    fun handle(action: IngressPacket.Disconnect) {
        if (action.pid == null) {
            return
        }

        gamesManager.removePlayer(action.pid)
    }

    fun handle(action: IngressPacket.AuthLogin) {
        val dbPlayer = database.getPlayer(action.pid)

        val creatureSeed = dbPlayer.toCreatureSeed()
        val playerSeed = dbPlayer.toPlayerSeed()

        gamesManager.addPlayer(creatureSeed, playerSeed)
    }

    fun handle(action: IngressPacket.PositionChange) {
        gamesManager.movePlayerTo(action.pid, action.targetPosition)
    }

    fun handle(action: IngressPacket.TerrainItemDrag) {
        gamesManager.dragItemOnTerrain(action.pid, action.itemInstanceId, action.targetPosition)
    }

    fun handle(action: IngressPacket.SpellUsage) {
        gamesManager.useSpell(action.pid, action.sid)
    }

    fun onPhysicsStep(deltaTime: Float) {
        gamesManager.onPhysicsStep(deltaTime)
    }

    fun handle(action: IngressPacket.BasicAttackStart) {
        gamesManager.startBasicAttacking(action.pid, action.targetCid)
    }

    fun handle(action: IngressPacket.BasicAttackStop) {
        gamesManager.stopBasicAttacking(action.pid)
    }

    fun handle(action: IngressPacket.PlayerStatsUpdateRequest) {
        gamesManager.requestPlayerStatsUpdate(action.pid)
    }
}
