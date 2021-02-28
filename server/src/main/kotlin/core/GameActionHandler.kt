package core

import core.maps.GameMapId
import infrastructure.database.Database
import infrastructure.udp.ingress.IngressPacket
import mu.KotlinLogging
import org.mini2Dx.gdx.math.Vector2

private val logger = KotlinLogging.logger {}

class GameActionHandler(
    private val gamesManager: GamesManager,
    private val database: Database
) {
    fun handle(action: IngressPacket.ConnectionHello) {
        logger.debug { action }
    }

    fun handle(action: IngressPacket.Disconnect) {
        logger.debug { action }

        if (action.pid == null) {
            return
        }

        gamesManager.removePlayer(action.pid)
    }

    fun handle(action: IngressPacket.AuthLogin) {
        logger.debug { action }

        val dbPlayer = database.getPlayer(action.pid)
        val playerCharacter = PlayerCharacter(
            id = dbPlayer.id,
            name = dbPlayer.name,
            health = dbPlayer.health,
            position = Vector2(100f, 100f)
        )

        gamesManager.addPlayer(playerCharacter, GameMapId(1u))
    }

    fun handle(action: IngressPacket.PositionChange) {
        logger.debug { action }

        val direction = action.direction.toDirection().toVector2()
        gamesManager.movePlayerBy(action.pid, direction)
    }
}
