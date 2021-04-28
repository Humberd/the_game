package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain

import clientjvm.exts.unsub
import clientjvm.global.ClientDataReceiver
import clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile.GroundTileScene
import godot.Area
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector2
import mu.KLogging
import pl.humberd.udp.packets.serverclient.TerrainUpdate

@RegisterClass
class TerrainScene : Area() {
    companion object : KLogging() {
        private const val GRID_SIZE = 20
        private const val TILE_SIZE = 64
    }

    private lateinit var tiles: Array<Array<GroundTileScene>>

    private val unsub by unsub()

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

        ClientDataReceiver.watch<TerrainUpdate>()
            .takeUntil(unsub)
            .subscribe { DrawTerrain(it) }
    }

    fun DrawTerrain(packet: TerrainUpdate) {
        val startX = packet.windowGridStartPositionX
        val startY = packet.windowGridStartPositionY
        val endX = startX + packet.windowWidth.toShort()
        val endY = startY + packet.windowHeight.toShort()

        logger.info { "$startX, $startY -> $endX, $endY" }

        ClearAllTiles()

        for (x in startX until endX) {
            for (y in startY until endY) {
                val offsetX = x - startX
                val offsetY = y - startY
                val index = offsetX * packet.windowHeight.toShort() + offsetY
                val spriteId = packet.spriteIds.get(index)
                tiles[x][y].setTile(spriteId)
            }
        }
    }

    fun ClearAllTiles() {
        tiles.forEach { it.forEach { it.unsetTile() } }
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }

}
