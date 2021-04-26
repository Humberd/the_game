package pl.humberd.udp.server.sender

import java.net.DatagramPacket
import java.nio.ByteBuffer

interface UdpSendQueuedPacket {
    fun toDatagram(buffer: ByteBuffer): DatagramPacket
}
