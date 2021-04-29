package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.creature

import clientjvm.exts.convert
import clientjvm.exts.packedScene
import clientjvm.exts.to2D
import clientjvm.exts.to3D
import clientjvm.global.AccountState
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import mu.KLogging
import pl.humberd.models.ApiVector2
import pl.humberd.udp.packets.serverclient.CreatureUpdate


@RegisterClass
class CreatureScene : Spatial() {
    companion object : KLogging() {
        val packedScene by packedScene()
    }


    @RegisterFunction
    override fun _ready() {
    }

    fun isMe(isMe: Boolean) {

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
        name = "Creature-${packet.cid}"
        update(packet.position)
    }

    private fun update(position: ApiVector2) {
        val radsAngle = transform.origin.to2D().angleToPoint(position.convert())
        translation = position.convert().to3D()
        if (radsAngle != 0.0) {
            //rotate the body
        }

        //start walking
        //isMoveing on next
    }
}
