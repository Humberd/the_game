package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.body

import clientjvm.exts.getNodeAs
import godot.AnimationPlayer
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class CreatureBodyScene : Spatial() {
    lateinit var animationPlayer: AnimationPlayer

    // take straight from the animation preview
    private val animationLength = 1.1667f

    @RegisterFunction
    override fun _ready() {
        animationPlayer = getNodeAs<AnimationPlayer>("AnimationPlayer").also {
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

    fun updateMovementSpeed(movementSpeed: Float) {
        animationPlayer.playbackSpeed = movementSpeed.toDouble() / 2 * animationLength
    }
}
