package clientjvm.scenes.statscollector

import clientjvm.exts.emitter
import clientjvm.global.ClientDataReceiver
import clientjvm.global.ClientDataSender
import clientjvm.global.GameStats
import clientjvm.global.GodotWorker
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import io.reactivex.rxjava3.core.Observable
import mu.KLogging
import pl.humberd.udp.packets.clientserver.PingRequest
import pl.humberd.udp.packets.serverclient.PingResponse
import java.util.concurrent.TimeUnit

@RegisterClass
class StatsCollectorScene : Spatial() {
    companion object : KLogging()

    private var pingRequestTimeMs = 0L
    private var fpsCounter = 0L

    private val unsub by emitter()

    @RegisterFunction
    override fun _ready() {
        watchPing()
        watchFps()
        watchBytes()
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        ++fpsCounter
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }

    private fun watchPing() {
        Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
            .observeOn(GodotWorker)
            .takeUntil(unsub)
            .subscribe {
                pingRequestTimeMs = System.currentTimeMillis()
                ClientDataSender.send(PingRequest())
            }

        ClientDataReceiver.watchFor<PingResponse>()
            .takeUntil(unsub)
            .subscribe {
                val pingTimeMs = System.currentTimeMillis() - pingRequestTimeMs
                GameStats.pingStream.onNext(pingTimeMs)
            }

    }

    private fun watchFps() {
        Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
            .observeOn(GodotWorker)
            .takeUntil(unsub)
            .subscribe {
                GameStats.fpsStream.onNext(fpsCounter)
                fpsCounter = 0
            }
    }

    private fun watchBytes() {
        Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
            .observeOn(GodotWorker)
            .takeUntil(unsub)
            .subscribe {
                GameStats.bytesSent.onNext(ClientDataSender.getBytes())
                GameStats.bytesReceived.onNext(ClientDataReceiver.getBytes())
            }
    }
}
