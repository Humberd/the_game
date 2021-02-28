package infrastructure.udp.ingress

import core.GameActionHandler
import core.types.DirectionByte
import core.types.PID
import infrastructure.udp.UdpConnectionPersistor
import infrastructure.udp.UdpClient
import infrastructure.udp.UdpClientStore
import utils.uByte
import utils.uInt
import java.lang.Exception
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class UdpIngressPacketHandler(
    private val gameActionHandler: GameActionHandler,
    private val udpClientStore: UdpClientStore,
    private val udpConnectionPersistor: UdpConnectionPersistor
) {

    init {
        udpConnectionPersistor.onRemoved {
            val clientId = it.getIdentifier()
            val pid = udpClientStore.getPID(clientId)
            udpClientStore.remove(clientId)
            gameActionHandler.handle(IngressPacket.Disconnect(pid))
        }
    }

    /**
     * First 4 bytes are 0x69 0x69 0x69 0x69 as a handshake
     */
    fun handle(packet: ByteBuffer, client: UdpClient) {
        val clientId = client.getIdentifier()

//        println(
//            "Packet from $clientId (${packet.limit()})${packet.array().sliceArray(0..packet.limit() - 1).toHex()}"
//        )

        if (packet.limit() < 5) {
            logger.warn { "Invalid packet size: ${packet.limit()}" }
            return
        }

        val handshake = packet.int
        if (handshake != 0x69696969) {
            logger.warn { "Invalid handshake: $handshake" }
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
                IngressPacketType.PING_REQUEST -> {
                    udpConnectionPersistor.register(client)
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
            logger.error { "Invalid frame size" }
        } catch (e: Exception) {
            logger.error(e) {}
        } catch (e: Error) {
            logger.error(e) {}
        }
    }

    private fun getPacketType(buffer: ByteBuffer): Int {
        return buffer.short.toInt()
    }

    private fun getPID(client: UdpClient): PID {
        return udpClientStore.getPID(client.getIdentifier()) ?: throw Error("Unknown PID")
    }
}
