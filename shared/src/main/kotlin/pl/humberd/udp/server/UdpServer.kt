package pl.humberd.udp.server

import mu.KotlinLogging
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean

abstract class UdpServer(name: String) : Thread(name) {
    private val running = AtomicBoolean(false)
    private val buffer = ByteBuffer.allocate(2048)

    protected val logger = KotlinLogging.logger {}

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
            buffer.clear()
        }

        logger.info { "Stopped" }
    }

    abstract fun onTick(buffer: ByteBuffer)
}
