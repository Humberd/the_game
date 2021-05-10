package clientjvm.global

import godot.PackedScene
import godot.Texture
import godot.global.GD

object AssetLoader {
    fun loadSprite(id: UShort) = GD.load<Texture>("res://assets/sprites/${id}.png")!!
    fun load3dModel(name: String) = GD.load<PackedScene>("res://assets/models-3d/$name/$name.glb")!!
}
