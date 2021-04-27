package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain

import clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile.GroundTileScene
import godot.Area
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector2
import mu.KLogging

@RegisterClass
class TerrainScene : Area() {
    companion object : KLogging() {
        private const val GRID_SIZE = 20
        private const val TILE_SIZE = 64
    }

    private lateinit var tiles: Array<Array<GroundTileScene>>

    @RegisterFunction
    override fun _ready() {
        tiles = Array(GRID_SIZE) { x ->
            Array(GRID_SIZE) { y ->
                (GroundTileScene.packedScene.instance() as GroundTileScene).also {
                    it.load(Vector2(x, y))
                    addChild(it)
                }
            }
        }
    }
}
