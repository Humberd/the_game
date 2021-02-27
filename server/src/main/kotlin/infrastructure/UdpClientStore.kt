package infrastructure

import core.types.PID

typealias ClientId = String

class UdpClientStore {
    private val clients = HashMap<ClientId, PID>()

    fun setPID(clientId: ClientId, pid: PID) {
        clients[clientId] = pid
    }

    fun remove(clientId: ClientId) {
        clients.remove(clientId)
    }

    fun getPID(clientId: ClientId): PID? {
        return clients[clientId]
    }
}
