package core

import core.maps.entities.Player
import core.types.CID
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
        val playerCharacter = Player(
            pid = dbPlayer.id,
            cid = CID.unique(),
            name = dbPlayer.name,
            health = dbPlayer.health,
            spriteId = dbPlayer.spriteId
        )

        gamesManager.addPlayer(playerCharacter, GameMapId(1u))
    }

    fun handle(action: IngressPacket.PositionChange) {
        val direction = action.direction.toDirection().toVector2()
        gamesManager.movePlayerBy(action.pid, direction)
    }

    fun handle(action: IngressPacket.TerrainItemDrag) {
        gamesManager.dragItemOnTerrain(action.pid, action.iid, action.targetPosition)
    }
}
