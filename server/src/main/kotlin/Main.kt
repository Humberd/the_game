import core.GameActionHandler
import core.GameState
import core.StateChangeNotifier
import infrastructure.UdpClientStore
import infrastructure.egress.UdpEgressPacketHandler
import infrastructure.egress.UpdEgressServer
import infrastructure.ingress.UdpIngressPacketHandler
import infrastructure.ingress.UdpIngressServer
import mu.KotlinLogging
import java.net.DatagramSocket

private val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Hello from server" }

    val udpClientStore = UdpClientStore()
    val egressPacketHandler = UdpEgressPacketHandler(udpClientStore)

    val stateChangeNotifier = StateChangeNotifier(egressPacketHandler)
    val gameState = GameState(stateChangeNotifier)
    val gameActionHandler = GameActionHandler(gameState)

    val ingressPacketHandler = UdpIngressPacketHandler(gameActionHandler, udpClientStore)

    val socket = DatagramSocket(4445)
    val udpEgressServer = UpdEgressServer(socket, egressPacketHandler)
    val udpIngressServer = UdpIngressServer(socket, ingressPacketHandler)

    udpEgressServer.start()
    udpIngressServer.start()
}
