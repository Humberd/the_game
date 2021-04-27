package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile

import clientjvm.exts.to3D
import godot.Sprite3D
import godot.annotation.RegisterClass
import godot.core.Vector2

@RegisterClass
class GroundTileScene : Sprite3D() {

    fun load(TILE_SIZE: Int, vector2: Vector2) {
        translate(vector2.to3D())
    }
}
