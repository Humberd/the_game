package infrastructure.udp

import java.util.concurrent.ConcurrentHashMap
import mu.KotlinLogging

private val TIMEOUT = 1000 * 5 // 5 seconds

private val logger = KotlinLogging.logger {}

class UdpConnectionPersistor : Thread("UdpConnectionPersistor") {
    private var callback: (UdpClient) -> Unit = {}
    private val lut = ConcurrentHashMap<UdpClient, Long>()

    override fun run() {
        logger.info { "Starting Udp Connection Persistor" }

        while (true) {
            val currentTime = System.currentTimeMillis()

            lut.forEach { client, lastPingTime ->
                if (currentTime - lastPingTime > TIMEOUT) {
                    lut.remove(client)
                    callback.invoke(client)
                }
            }

            sleep(1000)
        }
    }

    fun register(client: UdpClient) {
        lut.set(client, System.currentTimeMillis())
    }

    fun onRemoved(callback: (UdpClient) -> Unit) {
        this.callback = callback
    }
}
