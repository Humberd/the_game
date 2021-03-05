package core

import infrastructure.udp.ingress.IngressPacket
import java.util.concurrent.ConcurrentLinkedQueue

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class GameLoop(
    private val gameActionHandler: GameActionHandler
) : Thread("GameLoop") {
    private val queue = ConcurrentLinkedQueue<IngressPacket>()

    override fun run() {
        logger.info { "Starting Game Loop" }

        while (true) {
            if (!queue.isEmpty()) {
                val packet = queue.poll()
                logger.debug { packet }

                @Suppress("UNUSED_VARIABLE")
                val foo: Unit = when (packet) {
                    is IngressPacket.ConnectionHello -> Unit
                    is IngressPacket.Disconnect -> gameActionHandler.handle(packet)
                    is IngressPacket.AuthLogin -> gameActionHandler.handle(packet)
                    is IngressPacket.PositionChange -> gameActionHandler.handle(packet)
                    is IngressPacket.TerrainItemDrag -> gameActionHandler.handle(packet)
                }
            }
            sleep(10)
        }
    }

    fun requestAction(action: IngressPacket) {
        queue.add(action)
    }
}
