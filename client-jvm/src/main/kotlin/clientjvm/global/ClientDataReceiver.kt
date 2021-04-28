package clientjvm.global

import clientjvm.infrastructure.ClientUdpReceiveQueue
import godot.core.memory.GodotStatic
import io.reactivex.rxjava3.core.Observable
import pl.humberd.udp.packets.serverclient.ServerClientUdpPacket
import pl.humberd.udp.server.receiver.UdpReceiverService

object ClientDataReceiver : GodotStatic {
    private val receiveQueue = ClientUdpReceiveQueue()
    private val udpReceiverService: UdpReceiverService

    init {
        registerAsSingleton()

        udpReceiverService = UdpReceiverService(socket, receiveQueue)
        udpReceiverService.start()
    }

    override fun collect() {
        udpReceiverService.kill()
    }

    inline fun <reified T : ServerClientUdpPacket> watch(): Observable<T> {
        return _dataStream()
            .filter { it.packet is T }
            .map { it.packet as T }
    }

    fun _dataStream() = receiveQueue.data

}
