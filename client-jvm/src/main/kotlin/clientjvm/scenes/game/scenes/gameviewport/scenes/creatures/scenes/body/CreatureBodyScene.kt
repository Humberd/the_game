package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.body

import godot.AnimationPlayer
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.getNode

@RegisterClass
class CreatureBodyScene : Spatial() {
    lateinit var animationPlayer: AnimationPlayer

    @RegisterFunction
    override fun _ready() {
        animationPlayer = getNode<AnimationPlayer>("AnimationPlayer").also {
            it.getAnimation("default")?.loop = true
            it.currentAnimation = "default"
        }
    }

    fun startWalking() {
        if (animationPlayer.isPlaying()) {
            return
        }

        animationPlayer.play()
    }

    fun stopWalking() {
        animationPlayer.stop()
        animationPlayer.seek(0.0, true)
    }
}
