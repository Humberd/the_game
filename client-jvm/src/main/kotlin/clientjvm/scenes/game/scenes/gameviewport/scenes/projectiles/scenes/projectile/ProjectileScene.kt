package clientjvm.scenes.game.scenes.gameviewport.scenes.projectiles.scenes.projectile

import clientjvm.exts.packedScene
import godot.Spatial
import godot.annotation.RegisterClass
import mu.KLogging

@RegisterClass
class ProjectileScene : Spatial() {
    companion object : KLogging() {
        val packedScene by packedScene()
    }
}
