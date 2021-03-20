package core

import infrastructure.udp.ingress.IngressPacket
import mu.KotlinLogging
import utils.Milliseconds
import utils.ms
import utils.sec
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
    private val gameActionHandler: GameActionHandler
) : Thread("GameLoop") {
    private val queue = ConcurrentLinkedQueue<IngressPacket>()
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
            while (!queue.isEmpty()) {
                val packet = queue.poll()
//                logger.debug { packet }

                handleAction(packet)
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

    var currentTime = System.currentTimeMillis()

    private fun simulatePhysics() {
        val newTime = System.currentTimeMillis()
        val deltaTimeInMs = (newTime - currentTime).toFloat()
        val deltaTimeInSec = deltaTimeInMs / 1000
        currentTime = newTime

        // fixme: should use 1/60 deltaTime
        gameActionHandler.onPhysicsStep(deltaTimeInSec)
    }

    fun requestAction(action: IngressPacket) {
        queue.add(action)
    }

    private fun handleAction(action: IngressPacket) {
        return when (action) {
            is IngressPacket.ConnectionHello -> Unit
            is IngressPacket.Disconnect -> gameActionHandler.handle(action)
            is IngressPacket.AuthLogin -> gameActionHandler.handle(action)
            is IngressPacket.PositionChange -> gameActionHandler.handle(action)
            is IngressPacket.TerrainItemDrag -> gameActionHandler.handle(action)
            is IngressPacket.SpellUsage -> gameActionHandler.handle(action)
            is IngressPacket.BasicAttackStart -> gameActionHandler.handle(action)
            is IngressPacket.BasicAttackStop -> gameActionHandler.handle(action)
            is IngressPacket.PlayerStatsUpdateRequest -> gameActionHandler.handle(action)
        }
    }

    fun requestAsyncTask(task: AsyncGameTask): AsyncGameTask {
        asyncGameTasks.add(task)
        return task
    }

    fun requestAsyncTask(intervalTick: Milliseconds, callback: () -> Unit ): AsyncGameTask {
        // FIXME: 16.03.2021 Timer without end
        val indefinitely = 100000.sec
        return requestAsyncTask(AsyncGameTask(intervalTick, indefinitely, {}, callback))
    }

    fun requestAsyncTaskOnce(timeout: Milliseconds, callback: () -> Unit): AsyncGameTask {
        return requestAsyncTask(AsyncGameTask(timeout, 0.ms, {}, callback))
    }
}
