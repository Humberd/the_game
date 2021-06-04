package clientjvm.scenes.game.scenes.gameviewport.scenes.projectiles

import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import mu.KLogging

@RegisterClass
class ProjectilesScene : Spatial() {
    companion object : KLogging() {
    }

    @RegisterFunction
    override fun _ready() {
    }
}
