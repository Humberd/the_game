package clientjvm.global

import clientjvm.infrastructure.ClientUdpReceiveQueue
import godot.core.memory.GodotStatic
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

    fun hasNext() = receiveQueue.hasNext()
    fun popNext() = receiveQueue.popNext()
}
