import core.GameActionHandler
import core.GameLoop
import core.GamesManager
import core.StateChangeNotifier
import core.maps.ItemSchemaStore
import infrastructure.database.Database
import infrastructure.udp.ServerUdpReceiveQueue
import infrastructure.udp.UdpClientStore
import infrastructure.udp.UdpConnectionPersistor
import infrastructure.udp.egress.UdpEgressPacketHandler
import infrastructure.udp.egress.UpdEgressServer
import mu.KotlinLogging
import pl.humberd.udp.server.receiver.UdpReceiverService
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

    val udpConnectionPersistor = UdpConnectionPersistor()

    val socket = DatagramSocket(4445)
    val serverUdpReceiveQueue = ServerUdpReceiveQueue()
    val gameLoop = GameLoop(gameActionHandler, serverUdpReceiveQueue, udpClientStore)
    val udpReceiverService = UdpReceiverService(socket, serverUdpReceiveQueue)
    val udpEgressServer = UpdEgressServer(socket, egressPacketHandler)

    gameLoop.start()
    udpConnectionPersistor.start()
    udpEgressServer.start()
    udpReceiverService.start()
}
