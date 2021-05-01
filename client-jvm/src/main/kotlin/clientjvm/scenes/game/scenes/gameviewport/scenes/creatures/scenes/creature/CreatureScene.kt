package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.creature

import clientjvm.exts.*
import clientjvm.global.AccountState
import clientjvm.global.ClientDataReceiver
import clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.body.CreatureBodyScene
import clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.info.CreatureInfoScene
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.core.Vector3
import godot.getNode
import io.reactivex.rxjava3.core.Observable
import mu.KLogging
import pl.humberd.models.ApiVector2
import pl.humberd.models.CID
import pl.humberd.udp.packets.serverclient.CreatureDisappear
import pl.humberd.udp.packets.serverclient.CreaturePositionUpdate
import pl.humberd.udp.packets.serverclient.CreatureUpdate
import java.util.concurrent.TimeUnit


@RegisterClass
class CreatureScene : Spatial() {
    companion object : KLogging() {
        val packedScene by packedScene()
    }

    private var cid: CID = CID.empty()
        set(value) {
            name = "Creature-${value}"
            field = value
        }

    private lateinit var body: CreatureBodyScene
    private lateinit var infoScene: CreatureInfoScene

    private val movingStream by emitter()
    private val unsub by emitter()
    val onDestroyed = unsub as Observable<Boolean>

    @RegisterFunction
    override fun _ready() {
        body = getNode("Creature Body")
        infoScene = getNode("Viewport/CreatureInfoScene")

        ClientDataReceiver.watchFor<CreaturePositionUpdate>()
            .filter { it.cid == cid.notEmpty() }
            .takeUntil(unsub)
            .subscribe { update(it.position) }

        ClientDataReceiver.watchFor<CreatureDisappear>()
            .filter { it.cid == cid.notEmpty() }
            .takeUntil(unsub)
            .subscribe { queueFree() }

        ClientDataReceiver.watchFor<CreatureUpdate>()
            .filter { it.cid == cid.notEmpty() }
            .takeUntil(unsub)
            .subscribe { update(it) }

        movingStream
            .debounce(100, TimeUnit.MILLISECONDS)
            .takeUntil(unsub)
            .subscribe { body.stopWalking() }
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }

    fun initData(packet: CreatureUpdate) {
        val playerDetails = AccountState.playerDetails
        require(playerDetails != null)
        val isMe = playerDetails.cid == packet.cid
        if (!isMe) {
            getNode(NodePath("Player"))?.free()
        }

        update(packet)
    }

    private fun update(packet: CreatureUpdate) {
        cid = packet.cid

        update(packet.position)
        infoScene.update(packet)
    }

    private fun update(position: ApiVector2) {
        val radsAngle = transform.origin.to2D().angleToPoint(position.convert())
        translation = position.convert().to3D()
        if (radsAngle != 0.0) {
            body.rotation = Vector3(0, -radsAngle - Math.toRadians(90.0), 0)
        }

        movingStream.onNext(true)
        body.startWalking()
    }
}
