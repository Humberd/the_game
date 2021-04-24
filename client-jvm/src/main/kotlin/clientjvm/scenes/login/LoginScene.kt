package clientjvm.scenes.login

import godot.PanelContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class LoginScene : PanelContainer() {
    @RegisterFunction
    fun player1Pressed() {
        println("clicked player1")
    }
    @RegisterFunction
    fun player2Pressed() {
        println("clicked player2")
    }
}
