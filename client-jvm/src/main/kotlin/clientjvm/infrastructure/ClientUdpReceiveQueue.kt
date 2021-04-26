package clientjvm.infrastructure

import mu.KotlinLogging
import pl.humberd.misc.toHex
import pl.humberd.udp.packets.ReadBuffer
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket
import pl.humberd.udp.server.receiver.UdpReceiveQueue
import java.net.SocketAddress
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue

class ClientUdpReceiveQueue : UdpReceiveQueue {
    private val queue = ConcurrentLinkedQueue<ClientUdpReceiveQueuePacket>()

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun put(buffer: ByteBuffer, socketAddress: SocketAddress) {
        if (buffer.limit() < 5) {
            logger.warn { "Invalid packet size: ${buffer.limit()}" }
            return
        }

        val handshake = buffer.int
        if (handshake != 0x42424242) {
            logger.warn { "Invalid handshake: $handshake" }
            return
        }

        val readBuffer = ReadBuffer(buffer)
        val packetTypeValue = readBuffer.getShort().toInt()
        val packetType = ServerClientUdpPacket.Type.from(packetTypeValue)

        try {
            if (packetType == null) {
                logger.warn { "Unknown packet type ${packetTypeValue}" }
                return
            }

            val packet = packetType.serialize.invoke(readBuffer)

//            if (!packet.isHot()) {
            if (true) {
                logger.info { "---RECEIVE---" }
                logger.info { "$packet" }
                logger.info {
                    "$socketAddress (${buffer.position()}B) ${
                        buffer.array().sliceArray(0..buffer.position() - 1).toHex()
                    }"
                }
            }
            queue.add(ClientUdpReceiveQueuePacket(packet))
        } catch (e: BufferUnderflowException) {
            logger.error { "Invalid frame size" }
        } catch (e: Exception) {
            logger.error(e) {}
        } catch (e: Error) {
            logger.error(e) {}
        }
    }

    fun hasNext() = !queue.isEmpty()
    fun popNext() = queue.remove()
}
