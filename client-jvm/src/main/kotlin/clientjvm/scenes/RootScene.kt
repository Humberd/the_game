package clientjvm.scenes

import godot.Node
import godot.PackedScene
import godot.ResourceLoader
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.memory.GodotStatic
import pl.humberd.shared

@RegisterClass
class RootScene : Spatial() {
    lateinit var loginScene: PackedScene
    lateinit var gameScene: PackedScene

    // Called when the node enters the scene tree for the first time.
    @RegisterFunction
    override fun _ready() {
        shared.hello()
        loginScene = ResourceLoader.load("res://src/main/kotlin/clientjvm/scenes/login/LoginScene.tscn") as PackedScene
        RootSceneManager.initializeFromRoot(this)
        RootSceneManager.loadScene(RootSceneManager.SCENE.LOGIN)
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
