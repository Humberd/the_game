package clientjvm.scenes.login

import clientjvm.global.ClientDataSender
import clientjvm.scenes.RootSceneManager
import godot.PanelContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import pl.humberd.udp.packets.clientserver.AuthLogin

@RegisterClass
class LoginScene : PanelContainer() {
    @RegisterFunction
    fun player1Pressed() {
        login(1u)
    }
    @RegisterFunction
    fun player2Pressed() {
        login(2u)
    }

    private fun login(playerId: UInt) {
        RootSceneManager.loadScene(RootSceneManager.SCENE.GAME)
        ClientDataSender.send(AuthLogin(playerId))
    }
}
