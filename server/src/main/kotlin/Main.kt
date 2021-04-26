import core.GameActionHandler
import core.GameLoop
import core.GamesManager
import core.StateChangeNotifier
import core.maps.ItemSchemaStore
import infrastructure.database.Database
import infrastructure.udp.ServerUdpReceiveQueue
import infrastructure.udp.ServerUdpSendQueue
import infrastructure.udp.UdpClientStore
import infrastructure.udp.UdpConnectionPersistor
import mu.KotlinLogging
import pl.humberd.udp.server.receiver.UdpReceiverService
import pl.humberd.udp.server.sender.UdpSenderService
import java.net.DatagramSocket

private val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Hello from server" }

    ItemSchemaStore.readItemsFromJson();
    val database = Database()

    val udpClientStore = UdpClientStore()

    val serverUdpReceiveQueue = ServerUdpReceiveQueue()
    val serverUdpSendQueue = ServerUdpSendQueue(udpClientStore)


    val stateChangeNotifier = StateChangeNotifier(serverUdpSendQueue)
    val gameState = GamesManager(stateChangeNotifier)
    val gameActionHandler = GameActionHandler(gameState, database)

    val udpConnectionPersistor = UdpConnectionPersistor()

    val socket = DatagramSocket(4445)
    val gameLoop = GameLoop(gameActionHandler, serverUdpReceiveQueue, udpClientStore)
    val udpReceiverService = UdpReceiverService(socket, serverUdpReceiveQueue)
    val udpSenderService = UdpSenderService(socket, serverUdpSendQueue)

    gameLoop.start()
    udpConnectionPersistor.start()
    udpReceiverService.start()
    udpSenderService.start()
}
