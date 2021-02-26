package infrastructure

import java.net.DatagramPacket
import java.net.DatagramSocket

class UdpServer(private val packetHandler: UdpPacketHandler) : Thread() {
    private val socket: DatagramSocket
    private var running = false
    private var buffer = ByteArray(256)

    init {
        socket = DatagramSocket(4445)
    }

    override fun run() {
        running = true

        while (running) {
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)

            packetHandler.handle(buffer.toUByteArray(), packet.length)
        }

        socket.close()
    }
}
