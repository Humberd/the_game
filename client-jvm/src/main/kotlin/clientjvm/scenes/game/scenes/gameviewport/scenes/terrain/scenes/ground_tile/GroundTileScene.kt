package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile

import clientjvm.exts.packedScene
import clientjvm.exts.to3D
import clientjvm.global.AssetLoader
import godot.Sprite3D
import godot.annotation.RegisterClass
import godot.core.Vector2

@RegisterClass
class GroundTileScene : Sprite3D() {
    companion object {
        val packedScene by packedScene()
    }

    fun load(gridCoordinates: Vector2) {
        translate(gridCoordinates.to3D())
        name = "Tile(${gridCoordinates.x}, ${gridCoordinates.y})"
        unsetTile()
    }

    fun setTile(spriteId: UShort) {
        texture = AssetLoader.loadSprite(spriteId)
    }

    fun unsetTile() {
        texture = null
    }
}


