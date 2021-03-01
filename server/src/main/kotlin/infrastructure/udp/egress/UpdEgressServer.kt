package infrastructure.udp.egress

import java.net.DatagramSocket

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class UpdEgressServer(
    private val socket: DatagramSocket,
    private val packetHandler: UdpEgressPacketHandler
) : Thread("UdpEgressServer") {
    override fun run() {
        logger.info { "Starting Udp Egress Server" }

        while (true) {
            if (!packetHandler.hasItems()) {
                sleep(10)
                continue
            }

            val packet = packetHandler.readHead()

            socket.send(packet.prepareDatagram())
        }
    }
}
