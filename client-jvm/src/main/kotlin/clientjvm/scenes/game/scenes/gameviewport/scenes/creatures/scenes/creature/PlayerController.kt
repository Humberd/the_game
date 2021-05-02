package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.creature

import clientjvm.exts.*
import clientjvm.global.ClientDataSender
import clientjvm.global.CollisionLayer
import clientjvm.global.CrossScenesManager
import clientjvm.global.GodotWorker
import godot.InputEvent
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector3
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import mu.KLogging
import pl.humberd.udp.packets.clientserver.PositionChange
import java.util.concurrent.TimeUnit

@RegisterClass
class PlayerController : Spatial() {
    companion object : KLogging()

    private var mousePressed = false
    private val positionChangeStream = PublishSubject.create<Boolean>()

    private val unsub by emitter()

    @RegisterFunction
    override fun _ready() {
        positionChangeStream
            .throttleLast(200, TimeUnit.MILLISECONDS)
            .observeOn(GodotWorker)
            .switchMap { sendPositionChange() }
            .takeUntil(unsub)
            .subscribe {
                ClientDataSender.send(PositionChange(it[0].to2D().convert()))
            }
    }

    @RegisterFunction
    override fun _process(delta: Double) {
        if (mousePressed) {
            positionChangeStream.onNext(true)
        }
    }

    @RegisterFunction
    override fun _input(event: InputEvent) {
        if (mousePressed && event.isActionReleased("move")) {
            mousePressed = false
        }

        if (!mousePressed && event.isActionPressed("move")) {
            mousePressed = true
        }
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }

    private fun sendPositionChange(): Observable<Array<Vector3>> {
        val result = castCameraRays(
            collideWithAreas = false,
            collideWithBodies = true,
            layer = CollisionLayer.TERRAIN_PLATFORM.value.toLong()
        )

        if (result == null) {
            return Observable.empty()
        }

        val promise = Promise<Pair<Vector3, Vector3>, Array<Vector3>>(
            translation to result.position
        )
        CrossScenesManager.positionChangeRequest.onNext(promise)

        return promise.emitter
    }
}

