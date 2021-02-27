package core

import infrastructure.ingress.IngressPacket

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
        gameState.addPlayer(PlayerCharacter(action.pid))
    }

    fun handle(action: IngressPacket.ConnectionHello) {
        println("Connection hello 🖐")
    }

    fun handle(action: IngressPacket.Disconnect) {
        println(action)

        if (action.pid == null) {
            return
        }

        gameState.removePlayer(action.pid)
    }
}
