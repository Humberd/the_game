package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain

import clientjvm.exts.convert
import clientjvm.exts.unsub
import clientjvm.global.ClientDataReceiver
import clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile.GroundTileScene
import godot.Area
import godot.ArrayMesh
import godot.Mesh
import godot.MeshInstance
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.VariantArray
import godot.core.Vector2
import godot.core.Vector3
import godot.core.variantArrayOf
import mu.KLogging
import pl.humberd.udp.packets.serverclient.TerrainUpdate
import pl.humberd.udp.packets.serverclient.TerrainWallsUpdate

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

        ClientDataReceiver.watchFor<TerrainUpdate>()
            .takeUntil(unsub)
            .subscribe { drawTerrain(it) }

        ClientDataReceiver.watchFor<TerrainWallsUpdate>()
            .takeUntil(unsub)
            .subscribe { drawWalls(it) }
    }

    fun drawTerrain(packet: TerrainUpdate) {
        val startX = packet.windowGridStartPositionX
        val startY = packet.windowGridStartPositionY
        val endX = startX + packet.windowWidth.toShort()
        val endY = startY + packet.windowHeight.toShort()

        // clear all tiles
        tiles.forEach { it.forEach { it.unsetTile() } }

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

    fun drawWalls(packet: TerrainWallsUpdate) {
        logger.info { packet }
        for (chain in packet.chains) {
            val variantChain = variantArrayOf(*Array(chain.size) { chain[it].convert() })

            val arr = VariantArray<Any?>()
            arr.resize(8)
            arr.pushFront(variantChain)

            val arrayMesh = ArrayMesh().also {
                it.addSurfaceFromArrays(
                    primitive = Mesh.PrimitiveType.PRIMITIVE_LINE_STRIP.id,
                    arrays = arr
                )
            }

            val meshInstance = MeshInstance().also {
                it.mesh = arrayMesh
                it.name = "__wall"
                it.rotationDegrees = Vector3(90, 0, 0)
                it.translation = Vector3(0, 0.5, 0)
            }
            addChild(meshInstance)
        }
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }

}
