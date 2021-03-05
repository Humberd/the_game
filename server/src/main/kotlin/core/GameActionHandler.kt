package core

import core.types.GameMapId
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
        val playerCharacter = PlayerCharacter(
            id = dbPlayer.id,
            name = dbPlayer.name,
            health = dbPlayer.health,
        )

        gamesManager.addPlayer(playerCharacter, GameMapId(1u))
    }

    fun handle(action: IngressPacket.PositionChange) {
        val direction = action.direction.toDirection().toVector2()
        gamesManager.movePlayerBy(action.pid, direction)
    }

    fun handle(action: IngressPacket.TerrainItemDrag) {
        gamesManager.dragItemOnTerrain(action.pid, action.itemInstanceId, action.targetPosition)
    }
}
