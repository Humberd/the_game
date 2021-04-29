package clientjvm.global

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit

object GodotWorker : Scheduler() {
    val queue = ConcurrentLinkedQueue<Runnable>()

    internal class Worker : Scheduler.Worker() {
        var disposed = false;

        override fun dispose() {
            disposed = true
        }

        override fun isDisposed(): Boolean {
            return disposed
        }

        override fun schedule(run: Runnable?, delay: Long, unit: TimeUnit?): Disposable {
            println("Scheduled action ${run}")
            queue.add(run)

            return this
        }
    }

    override fun createWorker(): Scheduler.Worker {
        return Worker()
    }
}
