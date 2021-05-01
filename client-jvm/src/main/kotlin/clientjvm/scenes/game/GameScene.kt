package clientjvm.scenes.game

import clientjvm.exts.packedScene
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.memory.GodotStatic
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject

@RegisterClass
class GameScene : PanelContainer() {
    private val unsubscribe = PublishSubject.create<Boolean>()

    companion object {
        val packedScene by packedScene()
    }

    @RegisterFunction
    override fun _ready() {
        GameSceneManager.currentSceneObs
            .takeUntil(unsubscribe)
            .subscribe {
                println(it)
                if (it is GameSceneManager.CurrentScene.SceneIndeed) {
                    addChild(it.node)
                }
            }
    }

    override fun _onDestroy() {
        this.unsubscribe.onNext(true)
    }

    @RegisterFunction
    override fun _input(event: InputEvent) {
        if (event.isActionPressed("inventory")) {
            GameSceneManager.toggle(GameSceneManager.SCENE.INVENTORY)
        }
    }
}

object GameSceneManager : GodotStatic {
    private var inventoryScene =
        ResourceLoader.load("res://src/main/kotlin/clientjvm/scenes/game/scenes/inventory/InventoryScene.tscn") as PackedScene?

    enum class SCENE {
        NONE,
        INVENTORY
    }

    sealed class CurrentScene(val type: SCENE) {
        class NoneScene : CurrentScene(SCENE.NONE)
        data class SceneIndeed(var node: Node) : CurrentScene(SCENE.INVENTORY)
    }

    private val currentScene = BehaviorSubject.createDefault<CurrentScene>(CurrentScene.NoneScene())
    val currentSceneObs = currentScene as Observable<CurrentScene>

    init {
        registerAsSingleton()
    }

    override fun collect() {
        clearCurrentScene()
        currentScene.onNext(CurrentScene.NoneScene())
        inventoryScene = null
    }

    fun toggle(newSceneType: SCENE) {
        if (newSceneType === currentScene.value.type) {
            clearCurrentScene()
            currentScene.onNext(CurrentScene.NoneScene())
            return
        }

        val sceneNode: Node = when (newSceneType) {
            SCENE.NONE -> return
            SCENE.INVENTORY -> inventoryScene?.instance()!!
        }

        currentScene.onNext(CurrentScene.SceneIndeed(sceneNode))
    }

    private fun clearCurrentScene() {
        val scene = currentScene.value
        if (scene is CurrentScene.SceneIndeed) {
            scene.node.queueFree()
        }
    }

}
