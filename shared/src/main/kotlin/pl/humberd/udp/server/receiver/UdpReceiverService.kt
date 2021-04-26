package pl.humberd.udp.server.receiver

import pl.humberd.udp.server.UdpServer
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.ByteBuffer

class UdpReceiverService(
    private val socket: DatagramSocket,
    private val receiveQueue: UdpReceiveQueue
) : UdpServer("UdpReceiverServer") {

    override fun onTick(buffer: ByteBuffer) {
        val datagram = DatagramPacket(buffer.array(), buffer.array().size)
        socket.receive(datagram)
        buffer.limit(datagram.length)

        receiveQueue.put(buffer, datagram.socketAddress)
    }

}

