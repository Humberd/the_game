package core

import infrastructure.ingress.IngressPacket
import mu.KotlinLogging
import org.mini2Dx.gdx.math.Vector2

private val logger = KotlinLogging.logger {}

class GameActionHandler(
    private val gameState: GameState
) {
    fun handle(action: IngressPacket.PositionChange) {
        logger.debug { action }

        val direction = action.direction.toDirection().toVector2()
        gameState.movePlayerBy(action.pid, direction)
    }

    fun handle(action: IngressPacket.AuthLogin) {
        logger.debug { action }

        val playerCharacter = PlayerCharacter(action.pid)
        playerCharacter.position = Vector2(100f, 100f)

        gameState.addPlayer(playerCharacter)
    }

    fun handle(action: IngressPacket.ConnectionHello) {
        logger.debug { action }

    }

    fun handle(action: IngressPacket.Disconnect) {
        logger.debug { action }

        if (action.pid == null) {
            return
        }

        gameState.removePlayer(action.pid)
    }
}
