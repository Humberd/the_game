package infrastructure

import core.GameActionHandler
import core.types.DirectionByte
import infrastructure.ingress.IngressPacket
import infrastructure.ingress.IngressPacketType
import java.util.*

class UdpPacketHandler(private val gameActionHandler: GameActionHandler) {

    /**
     * First 3 bytes are 0x69 0x69 0x69 as a handshake
     */
    fun handle(packet: UByteArray, length: Int) {
        println(
            "Handling packet (${length}) ${
                Arrays.toString(packet.toByteArray().sliceArray(0..length - 1))
            }"
        )

        if (packet.size < 5) {
            println("Invalid packet")

            return
        }

        if (packet[0] != (0x69).toUByte() && packet[1] != (0x69).toUByte() && packet[2] != (0x69).toUByte()) {
            println("Invalid handshake")

            return
        }

        val packetType = getPacketType(packet)
        when (packetType) {
            IngressPacketType.POSITION_CHANGE -> {
                gameActionHandler.handle(IngressPacket.PositionChange(DirectionByte(packet[5])))
            }
            else -> println("Unknown packet type")
        }
    }

    private fun getPacketType(arr: UByteArray): Int {
        return arr[3].toInt().shl(8) and 0xFF00 or arr[4].toInt() and 0xFF
    }
}
