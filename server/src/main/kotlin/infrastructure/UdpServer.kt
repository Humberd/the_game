package infrastructure

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.ByteBuffer

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

            val byteBuffer = ByteBuffer.wrap(buffer, 0, packet.length)

            packetHandler.handle(byteBuffer, UdpClient(packet.socketAddress, packet.port))
        }

        socket.close()
    }
}
