package pl.humberd.udp.server

import java.net.DatagramPacket

interface UdpQueuedPacket {
    fun toDatagram(): DatagramPacket
}
