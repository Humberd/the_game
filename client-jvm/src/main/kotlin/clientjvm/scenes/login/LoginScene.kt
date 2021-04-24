package clientjvm.scenes.login

import godot.PanelContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class LoginScene : PanelContainer() {
    @RegisterFunction
    fun player1() {
        println("clicked player1")
    }
}
