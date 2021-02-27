import core.GameActionHandler
import core.GameState
import core.StateChangeNotifier
import infrastructure.UdpClientStore
import infrastructure.UdpPacketHandler
import infrastructure.UdpServer

fun main() {
    println("Hello from server")
    val stateChangeNotifier = StateChangeNotifier()
    val gameState = GameState(stateChangeNotifier)
    val gameActionHandler = GameActionHandler(gameState)
    val udpClientStore = UdpClientStore()
    val packetHandler = UdpPacketHandler(gameActionHandler, udpClientStore)
    val udpServer = UdpServer(packetHandler)
    udpServer.start()
}
