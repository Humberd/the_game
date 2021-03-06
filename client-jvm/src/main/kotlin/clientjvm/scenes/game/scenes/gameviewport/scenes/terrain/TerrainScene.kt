package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain

import clientjvm.exts.emitter
import clientjvm.exts.getNodeAs
import clientjvm.global.ClientDataReceiver
import clientjvm.global.CollisionLayer
import clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile.GroundTileScene
import godot.BoxShape
import godot.CollisionShape
import godot.Spatial
import godot.StaticBody
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector2
import godot.core.Vector3
import mu.KLogging
import pl.humberd.udp.packets.serverclient.TerrainUpdate

@RegisterClass
class TerrainScene : Spatial() {
    companion object : KLogging() {
        private const val GRID_SIZE = 20
    }

    private lateinit var content: Spatial
    private lateinit var tiles: Array<Array<GroundTileScene>>

    private val unsub by emitter()

    @RegisterFunction
    override fun _ready() {
        content = getNodeAs("Content")

        getNodeAs<CollisionShape>("Content/Platform/Collider").also {
            val boxShape = it.shape as BoxShape
            val offset = GRID_SIZE / 2
            boxShape.extents = Vector3(offset, 0.001, offset)
            it.translation = Vector3(offset, 0, offset)
            val platform = it.getParent()
            require(platform is StaticBody)
            platform.collisionLayer = CollisionLayer.TERRAIN_PLATFORM.value.toLong()
        }

        tiles = Array(GRID_SIZE) { x ->
            Array(GRID_SIZE) { y ->
                (GroundTileScene.packedScene.instance() as GroundTileScene).also {
                    it.load(Vector2(x, y))
                    content.addChild(it)
                }
            }
        }

        ClientDataReceiver.watchFor<TerrainUpdate>()
            .takeUntil(unsub)
            .subscribe { drawTerrain(it) }
    }

    fun drawTerrain(packet: TerrainUpdate) {
        val startX = packet.windowGridStartPositionX
        val startY = packet.windowGridStartPositionY
        val endX = startX + packet.windowWidth.toShort()
        val endY = startY + packet.windowHeight.toShort()

        // clear all tiles
        tiles.forEach {
            it.forEach {
                it.unsetTile()
                it.destroyObstacles()
            }
        }

        for (x in startX until endX) {
            for (y in startY until endY) {
                val offsetX = x - startX
                val offsetY = y - startY
                val index = offsetX * packet.windowHeight.toShort() + offsetY
                val spriteId = packet.spriteIds.get(index)
                tiles[x][y].setTile(spriteId)

                val obstacles = packet.obstacles.get(index)
                tiles[x][y].drawObstacles(obstacles)
            }
        }
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }
}
