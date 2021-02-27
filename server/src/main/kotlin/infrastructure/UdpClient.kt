package infrastructure

import java.net.SocketAddress

data class UdpClient(
    val address: SocketAddress,
) {
    fun getIdentifier(): String {
        return "${address}"
    }
}
