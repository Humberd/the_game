package clientjvm.scenes

import godot.Node
import godot.PackedScene
import godot.ResourceLoader
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.memory.GodotStatic

@RegisterClass
class RootScene : Spatial() {
    @RegisterFunction
    override fun _ready() {
        RootSceneManager.initializeFromRoot(this)
        RootSceneManager.loadScene(RootSceneManager.SCENE.LOGIN)
    }
}

object RootSceneManager : GodotStatic {
    private var loginScene = ResourceLoader.load("res://src/main/kotlin/clientjvm/scenes/login/LoginScene.tscn") as PackedScene?
    private var gameScene = ResourceLoader.load("res://src/main/kotlin/clientjvm/scenes/game/GameScene.tscn") as PackedScene?

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
        loginScene = null
        gameScene = null
    }

    fun loadScene(scene: SCENE) {
        if (currentScene?.scene === scene) {
            throw Error("Scene already instantiated")
        }

        clearCurrentScene()
        val instance: Node = when (scene) {
            SCENE.LOGIN -> loginScene?.instance()!!
            SCENE.GAME -> gameScene?.instance()!!
        }

        currentScene = CurrentScene(scene, instance).also {
            rootScene.addChild(it.instance)
        }
    }

    private fun clearCurrentScene() {
        currentScene?.instance?.queueFree()
        currentScene = null
    }


}
