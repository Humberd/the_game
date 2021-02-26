package core

import infrastructure.ingress.IngressPacket

class GameActionHandler {
    fun handle(action: IngressPacket.PositionChange) {
        println("Position change -> ${action.direction.toDirection()}")
    }
}
