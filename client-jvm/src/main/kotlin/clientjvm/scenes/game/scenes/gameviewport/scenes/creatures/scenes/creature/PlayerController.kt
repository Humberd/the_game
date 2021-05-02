package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.creature

import clientjvm.exts.castCameraRays
import clientjvm.exts.convert
import clientjvm.exts.emitter
import clientjvm.exts.to2D
import clientjvm.global.ClientDataSender
import godot.GlobalConstants
import godot.InputEvent
import godot.InputEventMouseButton
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import io.reactivex.rxjava3.subjects.PublishSubject
import pl.humberd.udp.packets.clientserver.PositionChange
import java.util.concurrent.TimeUnit

@RegisterClass
class PlayerController : Spatial() {
    private var mousePressed = false
    private val positionChangeStream = PublishSubject.create<Boolean>()

    private val unsub by emitter()

    @RegisterFunction
    override fun _ready() {
        positionChangeStream
            .throttleLast(200, TimeUnit.MILLISECONDS)
            .takeUntil(unsub)
            .subscribe { sendPositionChange() }
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        if (mousePressed) {
            positionChangeStream.onNext(true)
        }
    }

    @RegisterFunction
    override fun _input(event: InputEvent) {
        if (event is InputEventMouseButton) {
            if (event.buttonIndex == GlobalConstants.BUTTON_RIGHT) {
                // fixme: should be event.pressed
                mousePressed = event.doubleclick
            }
        }
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }

    private fun sendPositionChange() {
        val result = castCameraRays(collideWithAreas = true, collideWithBodies = false)

        if (result != null) {
            ClientDataSender.send(PositionChange(result.position.to2D().convert()))
        }
    }
}
