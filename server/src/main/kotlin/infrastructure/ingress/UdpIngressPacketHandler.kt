package infrastructure.ingress

import core.GameActionHandler
import core.types.DirectionByte
import core.types.PID
import infrastructure.UdpClient
import infrastructure.UdpClientStore
import utils.toHex
import utils.uByte
import utils.uInt
import java.lang.Exception
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

class UdpIngressPacketHandler(
    private val gameActionHandler: GameActionHandler,
    private val udpClientStore: UdpClientStore
) {

    /**
     * First 4 bytes are 0x69 0x69 0x69 0x69 as a handshake
     */
    fun handle(packet: ByteBuffer, client: UdpClient) {
        val clientId = client.getIdentifier()

        println(
            "Packet from $clientId (${packet.limit()})${packet.array().sliceArray(0..packet.limit() - 1).toHex()}"
        )

        if (packet.limit() < 5) {
            println("Invalid packet size")

            return
        }

        if (packet.int != 0x69696969) {
            println("Invalid handshake")

            return
        }


        try {
            when (getPacketType(packet)) {
                IngressPacketType.CONNECTION_HELLO -> {
                    udpClientStore.remove(clientId)
                    gameActionHandler.handle(IngressPacket.ConnectionHello())
                }
                IngressPacketType.DISCONNECT -> {
                    udpClientStore.remove(clientId)
                    gameActionHandler.handle(IngressPacket.Disconnect(udpClientStore.getPID(clientId)))
                }
                IngressPacketType.AUTH_LOGIN -> {
                    val pid = PID(packet.uInt())
                    udpClientStore.setPID(client, pid)

                    gameActionHandler.handle(
                        IngressPacket.AuthLogin(
                            pid
                        )
                    )
                }
                IngressPacketType.POSITION_CHANGE -> {
                    gameActionHandler.handle(
                        IngressPacket.PositionChange(
                            pid = getPID(client),
                            direction = DirectionByte(packet.uByte())
                        )
                    )
                }
                else -> println("Unknown packet type")
            }
        } catch (e: BufferUnderflowException) {
            println("Invalid frame size")
        } catch (e: Exception) {
            println(e)
        } catch (e: Error) {
            println(e)
        }
    }

    private fun getPacketType(buffer: ByteBuffer): Int {
        return buffer.short.toInt()
    }

    private fun getPID(client: UdpClient): PID {
        return udpClientStore.getPID(client.getIdentifier()) ?: throw Error("Unknown PID")
    }
}
