package pl.humberd.udp.server

interface UdpSendQueue {
    fun hasNext(): Boolean
    fun popNext(): UdpQueuedPacket
}

