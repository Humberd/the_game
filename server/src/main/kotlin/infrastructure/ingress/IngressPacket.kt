package infrastructure.ingress

import core.types.DirectionByte

object IngressPacketType {
    const val POSITION_CHANGE = 0x10
}

sealed class IngressPacket {
    class PositionChange(val direction: DirectionByte): IngressPacket()
}
