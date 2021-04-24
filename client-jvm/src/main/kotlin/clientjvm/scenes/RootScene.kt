package clientjvm

import godot.Node
import godot.PackedScene
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.core.NodePath
import godot.core.memory.GodotStatic

@RegisterClass
class RootScene : Spatial() {
    @RegisterProperty
    lateinit var loginScene: PackedScene

    @RegisterProperty
    lateinit var gameScene: PackedScene


    // Called when the node enters the scene tree for the first time.
    @RegisterFunction
    override fun _ready() {
        RootSceneManager.initializeFromRoot(this)
        println("xxxx")
        NodePath()
//        RootSceneManager.loadScene(RootSceneManager.SCENE.LOGIN)
    }

    // Called every frame. 'delta' is the elapsed time since the previous frame.
    @RegisterFunction
    override fun _process(delta: Double) {

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
        this.rootScene = rootScene
    }

    override fun collect() {
        this.clearCurrentScene()
    }

    fun loadScene(scene: SCENE) {
        if (currentScene?.scene === scene) {
            throw Error("Scene already instantiated")
        }

        this.clearCurrentScene()
        val instance: Node = when (scene) {
            SCENE.LOGIN -> rootScene.loginScene.instance()!!
            SCENE.GAME -> rootScene.gameScene.instance()!!
        }

        currentScene = CurrentScene(scene, instance).also {
            rootScene.addChild(it.instance)
        }
    }

    private fun clearCurrentScene() {
        currentScene?.instance?.free()
        currentScene = null
    }


}
