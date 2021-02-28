package infrastructure.udp.ingress

import core.types.DirectionByte
import core.types.PID

object IngressPacketType {
    const val CONNECTION_HELLO = 0x00
    const val DISCONNECT = 0x01
    const val PING_REQUEST = 0x02
    const val AUTH_LOGIN = 0x05
    const val POSITION_CHANGE = 0x10
}

sealed class IngressPacket {
    data class ConnectionHello(val _foo: String = "Connection hello 🖐"): IngressPacket()
    data class Disconnect(val pid: PID?): IngressPacket()
    data class AuthLogin(val pid: PID): IngressPacket()
    data class PositionChange(val pid: PID, val direction: DirectionByte): IngressPacket()
}
