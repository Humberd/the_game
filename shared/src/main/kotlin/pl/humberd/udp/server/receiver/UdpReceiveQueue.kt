package pl.humberd.udp.server.receiver

import java.net.SocketAddress
import java.nio.ByteBuffer

interface UdpReceiveQueue {
    fun put(buffer: ByteBuffer, socketAddress: SocketAddress)
}
