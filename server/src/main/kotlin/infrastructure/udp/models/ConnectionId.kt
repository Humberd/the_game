package infrastructure.udp.models

import java.net.SocketAddress

class ConnectionId(val socketAddress: SocketAddress) {
    val stringified: String

    init {
        this.stringified = socketAddress.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConnectionId

        if (stringified != other.stringified) return false

        return true
    }

    override fun hashCode(): Int {
        return stringified.hashCode()
    }

    override fun toString(): String {
        return "ConnectionId('$stringified')"
    }

}
