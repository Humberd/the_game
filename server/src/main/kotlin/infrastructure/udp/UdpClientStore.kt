package infrastructure.udp

import errors.UnknownPID
import infrastructure.udp.models.ConnectionId
import pl.humberd.models.PID

class UdpClientStore {
    private val connectionToPid = HashMap<ConnectionId, PID>()
    private val pidToConnection = HashMap<PID, ConnectionId>()

    fun setPID(connectionId: ConnectionId, pid: PID) {
        connectionToPid[connectionId] = pid
        pidToConnection[pid] = connectionId
    }

    fun remove(connectionId: ConnectionId) {
        val pid = connectionToPid[connectionId]
        if (pid == null) {
            println("No PID to remove for ${connectionId}")
            return
        }
        connectionToPid.remove(connectionId)
        pidToConnection.remove(pid)
    }

    fun getPidOrNull(connectionId: ConnectionId): PID? {
        return connectionToPid[connectionId]
    }

    fun getPid(connectionId: ConnectionId): PID {
        return connectionToPid[connectionId] ?: throw UnknownPID(connectionId)
    }

    fun getConnectionIdOrNull(pid: PID): ConnectionId? {
        return pidToConnection[pid]
    }

    fun getPidOrNull(clientId: Any): PID? {
        TODO()
    }
}
