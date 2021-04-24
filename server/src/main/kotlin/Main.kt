import core.GameActionHandler
import core.GameLoop
import core.GamesManager
import core.StateChangeNotifier
import core.maps.ItemSchemaStore
import infrastructure.database.Database
import infrastructure.udp.UdpClientStore
import infrastructure.udp.UdpConnectionPersistor
import infrastructure.udp.egress.UdpEgressPacketHandler
import infrastructure.udp.egress.UpdEgressServer
import infrastructure.udp.ingress.UdpIngressPacketHandler
import infrastructure.udp.ingress.UdpIngressServer
import mu.KotlinLogging
import java.net.DatagramSocket

private val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Hello from server" }

    ItemSchemaStore.readItemsFromJson();
    val database = Database()

    val udpClientStore = UdpClientStore()
    val egressPacketHandler = UdpEgressPacketHandler(udpClientStore)

    val stateChangeNotifier = StateChangeNotifier(egressPacketHandler)
    val gameState = GamesManager(stateChangeNotifier)
    val gameActionHandler = GameActionHandler(gameState, database)
    val gameLoop = GameLoop(gameActionHandler)

    val udpConnectionPersistor = UdpConnectionPersistor()
    val ingressPacketHandler = UdpIngressPacketHandler(gameLoop, udpClientStore, udpConnectionPersistor)

    val socket = DatagramSocket(4445)
    val udpEgressServer = UpdEgressServer(socket, egressPacketHandler)
    val udpIngressServer = UdpIngressServer(socket, ingressPacketHandler)

    gameLoop.start()
    udpConnectionPersistor.start()
    udpEgressServer.start()
    udpIngressServer.start()
}
