package infrastructure.udp.ingress

import core.GameActionHandler
import core.GameLoop
import core.types.DirectionByte
import core.types.PID
import errors.UnknownPID
import infrastructure.udp.ClientId
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
    private val gameLoop: GameLoop,
    private val udpClientStore: UdpClientStore,
    private val udpConnectionPersistor: UdpConnectionPersistor
) {

    init {
        udpConnectionPersistor.onRemoved {
            val clientId = it.getIdentifier()
            val pid = udpClientStore.getPID(clientId)
            udpClientStore.remove(clientId)
            gameLoop.requestAction(IngressPacket.Disconnect(pid))
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

        val packetType = getPacketType(packet)
        try {
            when (packetType) {
                IngressPacketType.CONNECTION_HELLO -> {
                    gameLoop.requestAction(IngressPacket.ConnectionHello())
                }
                IngressPacketType.DISCONNECT -> {
                    val pid = getPID(clientId)
                    udpClientStore.remove(clientId)
                    gameLoop.requestAction(IngressPacket.Disconnect(pid))
                }
                IngressPacketType.PING_REQUEST -> {
                    udpConnectionPersistor.register(client)
                }
                IngressPacketType.AUTH_LOGIN -> {
                    val pid = PID(packet.uInt())
                    udpClientStore.setPID(client, pid)

                    gameLoop.requestAction(
                        IngressPacket.AuthLogin(
                            pid
                        )
                    )
                }
                IngressPacketType.POSITION_CHANGE -> {
                    gameLoop.requestAction(
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
        } catch (e: UnknownPID) {
            logger.warn { e }
        } catch (e: Exception) {
            logger.error(e) {}
        } catch (e: Error) {
            logger.error(e) {}
        }

        if (packet.position() < packet.limit()) {
            logger.error { "Packet was not completely read from -> 0x${Integer.toHexString(packetType).padStart(2, '0')}" }
        }
    }

    private fun getPacketType(buffer: ByteBuffer): Int {
        return buffer.short.toInt()
    }

    private fun getPID(client: UdpClient): PID {
        return udpClientStore.getPID(client.getIdentifier()) ?: throw UnknownPID(client)
    }

    private fun getPID(clientId: ClientId): PID {
        return udpClientStore.getPID(clientId) ?: throw UnknownPID(clientId)
    }
}
