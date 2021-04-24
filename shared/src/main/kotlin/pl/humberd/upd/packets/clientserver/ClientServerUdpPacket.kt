package pl.humberd.upd.packets.clientserver

import pl.humberd.upd.models.ApiVector2
import pl.humberd.upd.packets.ReadBuffer
import pl.humberd.upd.packets.UdpPacket
import pl.humberd.upd.packets.WriteBuffer
import pl.humberd.upd.packets.clientserver.ClientServerUdpPacketType.*

sealed class ClientServerUdpPacket(type: ClientServerUdpPacketType) :
    UdpPacket<ClientServerUdpPacketType>(type) {

    class ConnectionHello() : ClientServerUdpPacket(CONNECTION_HELLO) {
        constructor(buffer: ReadBuffer) : this()

        override fun packData(buffer: WriteBuffer) {
            // nothing to pack
        }
    }

    class Disconnect() : ClientServerUdpPacket(DISCONNECT) {
        constructor(buffer: ReadBuffer) : this()

        override fun packData(buffer: WriteBuffer) {
            // nothing to pack
        }
    }

    class AuthLogin() : ClientServerUdpPacket(AUTH_LOGIN) {
        constructor(buffer: ReadBuffer) : this()

        override fun packData(buffer: WriteBuffer) {
            // nothing to pack
        }
    }

    data class PositionChange(
        val targetPosition: ApiVector2
    ) : ClientServerUdpPacket(POSITION_CHANGE) {

        constructor(buffer: ReadBuffer) : this(
            targetPosition = buffer.getVector2()
        )

        override fun packData(buffer: WriteBuffer) {
            buffer.putVector2(targetPosition)
        }

    }
}
