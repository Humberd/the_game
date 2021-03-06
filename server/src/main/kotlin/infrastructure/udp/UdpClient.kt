package infrastructure.udp

import java.net.SocketAddress

data class UdpClient(
    val address: SocketAddress,
) {
    fun getIdentifier(): String {
        return "${address}"
    }

    override fun toString(): String {
        return getIdentifier()
    }
}
