package core

import infrastructure.database.Database
import infrastructure.udp.ingress.IngressPacket
import infrastructure.udp.models.convert
import mu.KotlinLogging
import pl.humberd.udp.models.PID
import pl.humberd.udp.packets.clientserver.*

private val logger = KotlinLogging.logger {}

class GameActionHandler(
    private val gamesManager: GamesManager,
    private val database: Database
) {
    fun handle(packet: ConnectionHello) {
        // nothing
    }

    fun handle(packet: Disconnect, pid: PID?) {
        if (pid == null) {
            return
        }

        gamesManager.removePlayer(pid)
    }

    fun handle(packet: AuthLogin, savePid: (PID) -> Unit) {
        val pid = PID(packet.pid)
        savePid.invoke(pid)
        val dbPlayer = database.getPlayer(pid)

        val creatureSeed = dbPlayer.toCreatureSeed()
        val playerSeed = dbPlayer.toPlayerSeed()

        gamesManager.addPlayer(creatureSeed, playerSeed)
    }

    fun handle(packet: PositionChange, pid: PID) {
        gamesManager.movePlayerTo(pid, packet.targetPosition.convert())
    }

    fun handle(packet: BasicAttackStart, pid: PID) {
        gamesManager.startBasicAttacking(pid, packet.targetCid)
    }

    fun handle(packet: BasicAttackEnd, pid: PID) {
        gamesManager.stopBasicAttacking(pid)
    }

    fun handle(packet: PlayerStatsUpdateRequest, pid: PID) {
        gamesManager.requestPlayerStatsUpdate(pid)
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

    fun handle(action: IngressPacket.PlayerStatsUpdateRequest) {
        gamesManager.requestPlayerStatsUpdate(action.pid)
    }
}
