package core

import infrastructure.udp.ServerUdpReceiveQueue
import infrastructure.udp.ServerUdpReceiveQueuePacket
import infrastructure.udp.UdpClientStore
import mu.KotlinLogging
import pl.humberd.misc.exhaustive
import pl.humberd.models.Milliseconds
import pl.humberd.models.ms
import pl.humberd.models.sec
import pl.humberd.udp.packets.clientserver.*
import java.util.concurrent.ConcurrentLinkedQueue

private val logger = KotlinLogging.logger {}

data class AsyncGameTask(
    val intervalTick: Milliseconds, // 2ms
    val totalDuration: Milliseconds, // 50ms
    val onFinish: () -> Unit,
    val callback: () -> Unit
) {
    var lastTick: Long = 0
    var firstTick: Long = 0
    var scheduledForDeletion = false

    fun cancel() {
        scheduledForDeletion = true
    }
}


class GameLoop(
    private val gameActionHandler: GameActionHandler,
    private val serverUdpReceiveQueue: ServerUdpReceiveQueue,
    private val udpClientStore: UdpClientStore
) : Thread("GameLoop") {
    private val asyncGameTasks = ConcurrentLinkedQueue<AsyncGameTask>()

    init {
        instance = this
    }

    companion object {
        lateinit var instance: GameLoop
    }

    override fun run() {
        logger.info { "Starting Game Loop" }

        while (true) {
            while (serverUdpReceiveQueue.hasNext()) {
                try {
                    handleAction(serverUdpReceiveQueue.popNext())
                } catch (e: Error) {
                    logger.error(e) {}
                }
            }

            asyncGameTasks.forEach { task ->
                if (task.scheduledForDeletion) {
                    return@forEach
                }

                val currentTime = System.currentTimeMillis()
//                logger.debug { "Loop" }
                val isNew = task.firstTick == 0L
                if (isNew) {
                    task.lastTick = currentTime
                    task.firstTick = currentTime
                    task.callback.invoke()
                    return@forEach
                }
                val shouldInvoke = task.lastTick + task.intervalTick.value.toLong() <= currentTime
                if (shouldInvoke) {
                    task.lastTick = currentTime
                    task.callback.invoke()
                }

                val isOverTotalDuration = task.lastTick - task.firstTick >= task.totalDuration.value.toLong()
                if (isOverTotalDuration) {
                    task.scheduledForDeletion = true
                    task.onFinish.invoke()
                }
            }
            asyncGameTasks.removeAll { it.scheduledForDeletion }

            simulatePhysics()
            sleep(10)
        }
    }

    private fun handleAction(queuePacket: ServerUdpReceiveQueuePacket) {
        val (packet, connectionId) = queuePacket

        when (packet) {
            is ConnectionHello -> gameActionHandler.handle(packet)
            is Disconnect -> gameActionHandler.handle(packet, udpClientStore.getPidOrNull(connectionId))
            is AuthLogin -> gameActionHandler.handle(packet) { udpClientStore.setPID(connectionId, it) }
            is PositionChange -> gameActionHandler.handle(packet, udpClientStore.getPid(connectionId))
            is BasicAttackStart -> gameActionHandler.handle(packet, udpClientStore.getPid(connectionId))
            is BasicAttackEnd -> gameActionHandler.handle(packet, udpClientStore.getPid(connectionId))
            is PlayerStatsUpdateRequest -> gameActionHandler.handle(packet, udpClientStore.getPid(connectionId))
            is SpellUsage -> gameActionHandler.handle(packet, udpClientStore.getPid(connectionId))
            is PingRequest -> {
                val pid = udpClientStore.getPidOrNull(connectionId)
                if (pid == null) {
                    return
                }
                gameActionHandler.handle(packet, pid)
            }
            is SpellCastStart -> gameActionHandler.handle(packet, udpClientStore.getPid(connectionId))
            is SpellCastEnd -> gameActionHandler.handle(packet, udpClientStore.getPid(connectionId))
        }.exhaustive
    }

    var currentTime = System.currentTimeMillis()

    private fun simulatePhysics() {
        val newTime = System.currentTimeMillis()
        val deltaTimeInMs = (newTime - currentTime).toFloat()
        val deltaTimeInSec = deltaTimeInMs / 1000
        currentTime = newTime

        // fixme: should use 1/60 deltaTime
        gameActionHandler.onPhysicsStep(deltaTimeInSec)
    }

    fun requestAsyncTask(task: AsyncGameTask): AsyncGameTask {
        asyncGameTasks.add(task)
        return task
    }

    fun requestAsyncTask(intervalTick: Milliseconds, callback: () -> Unit): AsyncGameTask {
        // FIXME: 16.03.2021 Timer without end
        val indefinitely = 100000.sec
        return requestAsyncTask(AsyncGameTask(intervalTick, indefinitely, {}, callback))
    }

    fun requestAsyncTaskOnce(timeout: Milliseconds, callback: () -> Unit): AsyncGameTask {
        return requestAsyncTask(AsyncGameTask(timeout, 0.ms, {}, callback))
    }
}
