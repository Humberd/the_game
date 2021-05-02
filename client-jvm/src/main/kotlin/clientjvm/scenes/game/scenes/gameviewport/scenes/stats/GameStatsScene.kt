package clientjvm.scenes.game.scenes.gameviewport.scenes.stats

import clientjvm.exts.emitter
import clientjvm.global.GameStats
import godot.Control
import godot.Label
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.getNode
import mu.KLogging

@RegisterClass
class GameStatsScene : Control() {
    companion object : KLogging()

    private lateinit var fps: Label
    private lateinit var ping: Label

    private val unsub by emitter()

    @RegisterFunction
    override fun _ready() {
        fps = getNode("Fps")
        ping = getNode("Ping")

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
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }
}
