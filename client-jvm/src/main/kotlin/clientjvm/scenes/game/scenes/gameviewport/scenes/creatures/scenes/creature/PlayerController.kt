package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.creature

import clientjvm.exts.convert
import clientjvm.exts.to2D
import clientjvm.exts.unsub
import clientjvm.global.ClientDataSender
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector3
import io.reactivex.rxjava3.subjects.PublishSubject
import pl.humberd.udp.packets.clientserver.PositionChange
import java.util.concurrent.TimeUnit

@RegisterClass
class PlayerController : Spatial() {
    private lateinit var camera: Camera

    private var mousePressed = false
    private val positionChangeStream = PublishSubject.create<Boolean>()

    private val unsub by unsub()

    @RegisterFunction
    override fun _ready() {
        camera = getNode("Camera")

        positionChangeStream
            .throttleWithTimeout(200, TimeUnit.MILLISECONDS)
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
        val rayLength = 1000
        val mousePosition = getViewport()?.getMousePosition()!!
        val from = camera.projectRayOrigin(mousePosition)
        val to = from + camera.projectRayNormal(mousePosition) * rayLength
        val spaceState = getWorld()?.directSpaceState!!
        val result = spaceState.intersectRay(from, to, collideWithAreas = true, collideWithBodies = false)

        val position = result["position"]
        if (position is Vector3) {
            ClientDataSender.send(PositionChange(position.to2D().convert()))
        }
    }
}
