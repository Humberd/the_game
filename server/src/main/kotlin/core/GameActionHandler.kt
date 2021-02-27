package core

import infrastructure.ingress.IngressPacket
import org.mini2Dx.gdx.math.Vector2

class GameActionHandler(
    private val gameState: GameState
) {
    fun handle(action: IngressPacket.PositionChange) {
        println(action)

        val direction = action.direction.toDirection().toVector2()
        gameState.movePlayerBy(action.pid, direction)
    }

    fun handle(action: IngressPacket.AuthLogin) {
        println(action)
        val playerCharacter = PlayerCharacter(action.pid)
        playerCharacter.position = Vector2(100f, 100f)

        gameState.addPlayer(playerCharacter)
    }

    fun handle(action: IngressPacket.ConnectionHello) {
        println(action)
    }

    fun handle(action: IngressPacket.Disconnect) {
        println(action)

        if (action.pid == null) {
            return
        }

        gameState.removePlayer(action.pid)
    }
}
