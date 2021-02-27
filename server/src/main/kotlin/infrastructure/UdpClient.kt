package infrastructure

import java.net.SocketAddress

data class UdpClient(
    private val address: SocketAddress,
    private val port: Int
) {
    fun getIdentifier(): String {
        return "${address}"
    }
}
