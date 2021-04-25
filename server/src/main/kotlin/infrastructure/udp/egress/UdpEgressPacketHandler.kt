package infrastructure.udp.egress

import infrastructure.udp.UdpClientStore
import mu.KotlinLogging
import pl.humberd.udp.models.PID
import utils.Spammable
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.full.hasAnnotation

private val logger = KotlinLogging.logger {}


class UdpEgressPacketHandler(
    private val udpClientStore: UdpClientStore
) {
    private val queue = ConcurrentLinkedQueue<EgressPacketFrame>()

    fun hasItems(): Boolean {
        return !queue.isEmpty()
    }

    fun readHead(): EgressPacketFrame {
        return queue.remove()
    }

    fun notify(to: PID, dataPacket: EgressDataPacket) {
        @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
        if (!dataPacket::class.hasAnnotation<Spammable>()) {
//            logger.debug { "${to} -> ${dataPacket}" }
        }
        val connectionId = udpClientStore.getConnectionIdOrNull(to)
        if (connectionId == null) {
            println("Client not found")
            return
        }

        queue.add(EgressPacketFrame(connectionId, dataPacket))
    }
}
