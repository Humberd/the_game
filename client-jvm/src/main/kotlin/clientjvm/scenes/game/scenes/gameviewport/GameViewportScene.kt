package clientjvm.scenes.game.scenes.gameviewport

import clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.TerrainScene
import godot.Spatial
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.getNode
import mu.KLogging

@RegisterClass
class GameViewportScene : Spatial() {
    companion object : KLogging()

    private lateinit var terrainScene: TerrainScene

    @RegisterFunction
    override fun _ready() {
        terrainScene = getNode("Terrain")
    }

}
