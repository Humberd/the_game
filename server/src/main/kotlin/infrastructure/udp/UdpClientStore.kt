package infrastructure.udp

import core.types.PID

typealias ClientId = String

class UdpClientStore {
    private val clientObjects = HashMap<ClientId, UdpClient>()
    private val clients = HashMap<ClientId, PID>()
    private val pids = HashMap<PID, ClientId>()

    fun setPID(client: UdpClient, pid: PID) {
        val clientId = client.getIdentifier()
        clientObjects[clientId] = client
        clients[clientId] = pid
        pids[pid] = clientId
    }

    fun remove(clientId: ClientId) {
        val pid = clients[clientId]
        if (pid == null) {
            println("No client to remove")
            return
        }
        clientObjects.remove(clientId)
        clients.remove(clientId)
        pids.remove(pid)
    }

    fun getPID(clientId: ClientId): PID? {
        return clients[clientId]
    }

    fun getClient(pid: PID): UdpClient? {
        return clientObjects[pids[pid]]
    }
}
