package pl.humberd.udp.server

import mu.KotlinLogging
import java.net.DatagramSocket
import java.util.concurrent.atomic.AtomicBoolean

class UdpSenderServer(
    private val socket: DatagramSocket,
    private val sendQueue: UdpSendQueue
) : Thread("UpdSenderServer") {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val running = AtomicBoolean(false)

    fun kill() {
        logger.info { "Stopping" }
        running.set(false)
    }

    override fun run() {
        logger.info { "Starting" }
        running.set(true)

        while (running.get()) {
            if (!sendQueue.hasNext()) {
                continue
            }

            socket.send(sendQueue.popNext().toDatagram())
        }

        logger.info { "Stopped" }
    }
}
