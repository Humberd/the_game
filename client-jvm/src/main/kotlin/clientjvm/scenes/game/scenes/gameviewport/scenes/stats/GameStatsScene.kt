package clientjvm.scenes.game.scenes.gameviewport.scenes.stats

import clientjvm.exts.emitter
import clientjvm.exts.getNodeAs
import clientjvm.global.GameStats
import godot.Control
import godot.Label
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import mu.KLogging
import pl.humberd.misc.formatBytes

@RegisterClass
class GameStatsScene : Control() {
    companion object : KLogging()

    private lateinit var fps: Label
    private lateinit var ping: Label
    private lateinit var bytesSent: Label
    private lateinit var bytesReceived: Label

    private val unsub by emitter()

    @RegisterFunction
    override fun _ready() {
        fps = getNodeAs("Fps")
        ping = getNodeAs("Ping")
        bytesSent = getNodeAs("BytesSent")
        bytesReceived = getNodeAs("BytesReceived")

        GameStats.pingStream
            .startWithItem(0)
            .takeUntil(unsub)
            .subscribe {
                ping.text = "${it}ms"
            }

        GameStats.fpsStream
            .startWithItem(0)
            .takeUntil(unsub)
            .subscribe {
                fps.text = "${it}fps"
            }

        GameStats.bytesSent
            .startWithItem(0)
            .takeUntil(unsub)
            .subscribe {
                bytesSent.text = "Up ${formatBytes(it)}"
            }

        GameStats.bytesReceived
            .startWithItem(0)
            .takeUntil(unsub)
            .subscribe {
                bytesReceived.text = "Down ${formatBytes(it)}"
            }
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }
}
