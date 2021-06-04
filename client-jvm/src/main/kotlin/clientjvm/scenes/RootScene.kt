package clientjvm.scenes

import clientjvm.global.*
import clientjvm.scenes.game.GameScene
import clientjvm.scenes.login.LoginScene
import godot.Node
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.memory.GodotStatic
import godot.global.GD
import pl.humberd.udp.packets.clientserver.ConnectionHello
import pl.humberd.udp.packets.clientserver.Disconnect

@RegisterClass
class RootScene : Spatial() {

    @RegisterFunction
    override fun _ready() {
        AccountState.init()
        ClientDataReceiver._init()
        ClientDataSender.init()

        RootSceneManager.initializeFromRoot(this)
        RootSceneManager.loadScene(RootSceneManager.SCENE.LOGIN)

        ClientDataSender.send(ConnectionHello())
    }

    @RegisterFunction
    override fun _exitTree() {
        ClientDataSender.send(Disconnect())
    }

    override fun _onDestroy() {
        socket.close()
        ClientDataSender.kill()
        ClientDataReceiver._kill()
        AccountState.kill()
    }

    @RegisterFunction
    override fun _physicsProcess(delta: Double) {
        while (!GodotWorker.queue.isEmpty()) {
            GodotWorker.queue.remove().run()
        }
    }
}

object RootSceneManager : GodotStatic {
    enum class SCENE {
        LOGIN,
        GAME
    }

    private data class CurrentScene(
        val scene: SCENE,
        val instance: Node
    )

    private lateinit var rootScene: RootScene
    private var currentScene: CurrentScene? = null

    init {
        registerAsSingleton()
    }

    fun initializeFromRoot(rootScene: RootScene) {
        RootSceneManager.rootScene = rootScene
    }

    override fun collect() {
        clearCurrentScene()
    }

    fun loadScene(scene: SCENE) {
        if (currentScene?.scene === scene) {
            throw Error("Scene already instantiated")
        }

        clearCurrentScene()
        val instance: Node = when (scene) {
            SCENE.LOGIN -> LoginScene.packedScene.instance()!!
            SCENE.GAME -> GameScene.packedScene.instance()!!
        }

        currentScene = CurrentScene(scene, instance).also {
            rootScene.addChild(it.instance)
        }
    }

    private fun clearCurrentScene() {
        val node = currentScene?.instance
        if (node != null && GD.isInstanceValid(node)) {
            node.queueFree()
        }
        currentScene = null
    }


}
