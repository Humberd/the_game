package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures

import clientjvm.exts.unsub
import clientjvm.global.ClientDataReceiver
import clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.creature.CreatureScene
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import pl.humberd.models.CID
import pl.humberd.udp.packets.serverclient.CreatureUpdate

@RegisterClass
class CreaturesScene : Spatial() {
    private val unsub by unsub()
    private val allCreatures = HashMap<CID, CreatureScene>()

    @RegisterFunction
    override fun _ready() {
        ClientDataReceiver.watchFor<CreatureUpdate>()
            .takeUntil(unsub)
            .subscribe { updateCreature(it) }
    }

    private fun updateCreature(packet: CreatureUpdate) {
        if (allCreatures.containsKey(packet.cid)) {
            return
        }

        val creatureScene = CreatureScene.packedScene.instance() as CreatureScene
        addChild(creatureScene)
        creatureScene.initData(packet)
        creatureScene.onDestroyed
            .take(1)
            .subscribe {
                allCreatures.remove(packet.cid)
            }

        allCreatures[packet.cid] = creatureScene
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }
}
