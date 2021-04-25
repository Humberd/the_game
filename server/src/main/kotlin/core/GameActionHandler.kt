package core

import core.types.PID
import infrastructure.database.Database
import infrastructure.udp.ingress.IngressPacket
import infrastructure.udp.models.convert
import mu.KotlinLogging
import pl.humberd.udp.packets.clientserver.AuthLogin
import pl.humberd.udp.packets.clientserver.ConnectionHello
import pl.humberd.udp.packets.clientserver.Disconnect
import pl.humberd.udp.packets.clientserver.PositionChange

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
