package infrastructure.udp

import infrastructure.udp.models.ConnectionId
import mu.KotlinLogging
import pl.humberd.misc.toHex
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.clientserver.ClientServerUdpPacket.Type
import pl.humberd.udp.server.receiver.UdpReceiveQueue
import java.net.SocketAddress
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue

class ServerUdpReceiveQueue : UdpReceiveQueue {
    private val queue = ConcurrentLinkedQueue<ServerUdpReceiveQueuePacket>()

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun hasNext() = !queue.isEmpty()

    fun popNext() = queue.remove()

    override fun put(buffer: ByteBuffer, socketAddress: SocketAddress) {
        val connectionId = ConnectionId(socketAddress)

        logger.info {
            "Packet from $connectionId (${buffer.limit()})${buffer.array().sliceArray(0..buffer.limit() - 1).toHex()}"
        }

        if (buffer.limit() < 5) {
            logger.warn { "Invalid packet size: ${buffer.limit()}" }
            return
        }

        val handshake = buffer.int
        if (handshake != 0x69696969) {
            logger.warn { "Invalid handshake: $handshake" }
            return
        }

        val readBuffer = ReadBuffer(buffer)
        val packetTypeValue = readBuffer.getShort().toInt()
        val packetType = Type.from(packetTypeValue)

        try {
            if (packetType == null) {
                logger.warn { "Unknown packet type ${packetTypeValue}" }
                return
            }

            queue.add(ServerUdpReceiveQueuePacket(packetType.serialize.invoke(readBuffer), connectionId))
        } catch (e: BufferUnderflowException) {
            logger.error { "Invalid frame size" }
        } catch (e: Exception) {
            logger.error(e) {}
        } catch (e: Error) {
            logger.error(e) {}
        }
    }
}
