package infrastructure.udp

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue

private val TIMEOUT = 1000 * 5 // 5 seconds

class UdpConnectionPersistor : Thread("PingController") {
    private var callback: (UdpClient) -> Unit = {}
    private val players = PriorityBlockingQueue<Pair>(10) { o1, o2 ->
        o1.time.compareTo(o2.time)
    }
    private val lut = ConcurrentHashMap<UdpClient, Pair>()

    override fun run() {
        while (true) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - players.peek().time > TIMEOUT) {
                val player = players.poll()
                lut.remove(player.udpClient)
                callback.invoke(player.udpClient)

                continue
            }

            sleep(1000)
        }
    }

    fun register(client: UdpClient) {
        val oldPair = lut[client]
        if (oldPair != null) {
            oldPair.time = System.currentTimeMillis()
            return
        }

        val newPair = Pair(client, System.currentTimeMillis())
        lut.put(client, newPair)
        players.put(newPair)
    }

    fun onRemoved(callback: (UdpClient) -> Unit) {
        this.callback = callback
    }

    inner class Pair(val udpClient: UdpClient, var time: Long)
}
