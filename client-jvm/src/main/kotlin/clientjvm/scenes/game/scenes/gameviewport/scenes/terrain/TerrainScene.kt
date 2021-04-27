package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain

import clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile.GroundTileScene
import godot.Area
import godot.PackedScene
import godot.ResourceLoader
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector2

@RegisterClass
class TerrainScene : Area() {
    companion object {
        private const val GRID_SIZE = 20
        private const val TILE_SIZE = 64
    }

    private val tileScene: PackedScene =
        ResourceLoader.load("res://src/main/kotlin/clientjvm/scenes/game/scenes/gameviewport/scenes/terrain/scenes/ground_tile/GroundTileScene.tscn") as PackedScene
    private lateinit var tiles: Array<Array<GroundTileScene>>

    @RegisterFunction
    override fun _ready() {
//        addChild(tileScene.instance() as GroundTileScene)
        tiles = Array(GRID_SIZE) { x ->
            Array(GRID_SIZE) { y ->
                (tileScene.instance() as GroundTileScene).also {
                    it.load(TILE_SIZE, Vector2(x, y))
                    addChild(it)
                }
            }
        }
    }
}
