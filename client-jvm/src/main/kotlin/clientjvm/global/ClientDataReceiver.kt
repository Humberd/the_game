package clientjvm.global

import clientjvm.infrastructure.ClientUdpReceiveQueue
import io.reactivex.rxjava3.core.Observable
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket
import pl.humberd.udp.server.receiver.UdpReceiverService

object ClientDataReceiver {
    private val receiveQueue = ClientUdpReceiveQueue()
    private lateinit var udpReceiverService: UdpReceiverService

    fun _init() {
        udpReceiverService = UdpReceiverService(socket, receiveQueue)
        udpReceiverService.start()
    }

    fun _kill() {
        udpReceiverService.kill()
    }

    inline fun <reified T : ServerClientUdpPacket> watchFor(): Observable<T> {
        return _dataStream()
            .filter { it.packet is T }
            .map { it.packet as T }
            .observeOn(GodotWorker)
    }

    fun _dataStream() = receiveQueue.data

}
