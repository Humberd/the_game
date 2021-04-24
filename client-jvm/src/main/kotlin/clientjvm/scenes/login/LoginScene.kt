package clientjvm.scenes.login

import clientjvm.scenes.RootSceneManager
import godot.PanelContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class LoginScene : PanelContainer() {
    @RegisterFunction
    fun player1Pressed() {
        RootSceneManager.loadScene(RootSceneManager.SCENE.GAME)
    }
    @RegisterFunction
    fun player2Pressed() {
        println("clicked player2")
    }
}
