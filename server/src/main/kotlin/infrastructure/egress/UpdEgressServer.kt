package infrastructure.egress

import java.net.DatagramSocket

class UpdEgressServer(
    private val socket: DatagramSocket,
    private val packetHandler: UdpEgressPacketHandler
) : Thread("UdpEgressServer") {
    override fun run() {
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
