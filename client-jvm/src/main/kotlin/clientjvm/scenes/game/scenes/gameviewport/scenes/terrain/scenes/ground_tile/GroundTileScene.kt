package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile

import clientjvm.exts.godotLazy
import clientjvm.exts.to3D
import godot.PackedScene
import godot.Sprite3D
import godot.annotation.RegisterClass
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class GroundTileScene : Sprite3D() {
    companion object {
        val packedScene: PackedScene by godotLazy {
            val path = this::class.java.canonicalName
                .replace(".Companion", "")
                .replace(".", "/")

            val resourcePath = "res://src/main/kotlin/$path.tscn"

            GD.load(resourcePath)
        }
    }

    fun load(gridCoordinates: Vector2) {
        translate(gridCoordinates.to3D())
        name = "Tile(${gridCoordinates.x}, ${gridCoordinates.y})"
    }
}


