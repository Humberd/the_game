package clientjvm.global

import godot.Texture
import godot.global.GD

object AssetLoader {
    fun loadSprite(id: UShort) = GD.load<Texture>("res://assets/sprites/${id}.png")
}
