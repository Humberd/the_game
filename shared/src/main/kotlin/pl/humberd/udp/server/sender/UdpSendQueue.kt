package pl.humberd.udp.server.sender

interface UdpSendQueue {
    fun hasNext(): Boolean
    fun popNext(): UdpSendQueuedPacket
}

