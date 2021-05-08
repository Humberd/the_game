package pl.humberd.udp.server

import mu.KLogging
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

abstract class UdpServer(name: String) : Thread(name) {
    companion object : KLogging()

    private val running = AtomicBoolean(false)
    private val buffer = ByteBuffer.allocate(2048)

    private var bytesCounter = 0L

    fun kill() {
        logger.info { "Stopping" }
        running.set(false)
    }

    override fun run() {
        logger.info { "Starting" }
        running.set(true)

        while (running.get()) {
            try {
                onTick(buffer)
            } catch (e: SocketException) {
                logger.info { e.message }
                kill()
            }
            bytesCounter += buffer.position() / 8
            buffer.clear()
        }

        logger.info { "Stopped" }
    }

    abstract fun onTick(buffer: ByteBuffer)

    fun getBytes() = bytesCounter
}
