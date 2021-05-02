package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.info.scenes.bar

import godot.ColorRect
import godot.Control
import godot.GlobalConstants
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.getNode
import mu.KLogging

@RegisterClass
class CreatureInfoBarScene : Control() {
    companion object : KLogging()

    private lateinit var valueBar: ColorRect

    var width = 0f
        set(value) {
            require(value >= 0 && value <= 1) { value }

            valueBar.setAnchor(GlobalConstants.MARGIN_RIGHT, value.toDouble(), true)

            field = value
        }

    @RegisterFunction
    override fun _ready() {
        valueBar = getNode("Value")

        width = 0.5f
    }

    fun setColor(color: Color) {
        valueBar.color = color
    }
}
