import core.GameActionHandler
import infrastructure.UdpPacketHandler
import infrastructure.UdpServer

fun main() {
    println("Hello from server")
    val gameActionHandler = GameActionHandler()
    val packetHandler = UdpPacketHandler(gameActionHandler)
    val udpServer = UdpServer(packetHandler)
    udpServer.start()
}
