package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.creature

import clientjvm.exts.*
import clientjvm.global.AccountState
import clientjvm.global.ClientDataReceiver
import clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.body.CreatureBodyScene
import clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.info.CreatureInfoScene
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.core.NodePath
import godot.core.Vector3
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
    private lateinit var rigidBody: RigidBody
    private lateinit var collisionMesh: MeshInstance
    private lateinit var collisionShape: CollisionShape
    private lateinit var tween: Tween

    private val colliderColorHover = Color.html("b2cbe4de")
    private val colliderColorNormal = Color.html("33cbe4de")

    private val movingStream by emitter()
    private val unsub by emitter()
    val onDestroyed = unsub as Observable<Boolean>

    @RegisterFunction
    override fun _ready() {
        body = getNodeAs("Creature Body")
        infoScene = getNodeAs("Viewport/CreatureInfoScene")
        rigidBody = getNodeAs("Collider")
        collisionMesh = getNodeAs("Collider/CollisionMesh")
        collisionShape = getNodeAs("Collider/CollisionShape")
        tween = getNodeAs("Tween")

        ClientDataReceiver.watchFor<CreaturePositionUpdate>()
            .filter { it.cid == cid.notEmpty() }
            .takeUntil(unsub)
            .subscribe { updatePosition(it.position) }

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

    @RegisterFunction
    override fun _input(event: InputEvent) {
        if (event is InputEventMouseMotion) {
            val result = castCameraRays(collideWithAreas = false, collideWithBodies = true)

            val cylinder = collisionMesh.mesh as CylinderMesh
            val material = cylinder.material as SpatialMaterial
            if (result == null || result.collider !== rigidBody) {
                material.albedoColor = colliderColorNormal
            } else {
                material.albedoColor = colliderColorHover

            }
        }
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

        updatePosition(packet.position)
        infoScene.update(packet)
        updateBodyRadius(packet.bodyRadius)
    }

    private fun updatePosition(position: ApiVector2) {
        val radsAngle = transform.origin.to2D().angleToPoint(position.convert())
        translation = position.convert().to3D()
        if (radsAngle != 0.0) {
            val newRotation = Vector3(0, -radsAngle - Math.toRadians(90.0), 0)
            body.rotation = newRotation
            //fixme: https://github.com/utopia-rise/godot-kotlin-jvm/pull/219
//            tween.interpolateProperty(
//                this,
//                NodePath("rotation"),
//                body.rotation,
//                newRotation,
//                0.1,
//                Tween.TRANS_LINEAR,
//                Tween.EASE_IN_OUT
//            )
//            tween.start()
        }

        movingStream.onNext(true)
        body.startWalking()
    }

    private fun updateBodyRadius(bodyRadius: Float) {
        val scale = Vector3(bodyRadius, 1, bodyRadius)
        collisionMesh.scale = scale
        collisionShape.scale = Vector3(scale)

    }
}
