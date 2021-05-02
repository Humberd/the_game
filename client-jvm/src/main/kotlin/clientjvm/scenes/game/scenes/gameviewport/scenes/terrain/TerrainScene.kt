package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain

import clientjvm.addons.godotnavigationlite.DetourNavigationMesh
import clientjvm.exts.*
import clientjvm.global.ClientDataReceiver
import clientjvm.global.CrossScenesManager
import clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile.GroundTileScene
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.*
import mu.KLogging
import pl.humberd.udp.packets.serverclient.TerrainUpdate
import pl.humberd.udp.packets.serverclient.TerrainWallsUpdate

@RegisterClass
class TerrainScene : Spatial() {
    companion object : KLogging() {
        private const val GRID_SIZE = 20
    }

    private lateinit var detourNavigationMesh: DetourNavigationMesh
    private lateinit var content: Spatial
    private lateinit var tiles: Array<Array<GroundTileScene>>

    private val unsub by emitter()

    @RegisterFunction
    override fun _ready() {
        detourNavigationMesh = DetourNavigationMesh(getNode("Navigation/DetourNavigationMesh"))
        content = getNode("Navigation/Content")

        getNode<CollisionShape>("Navigation/Content/Platform/Collider").also {
            val boxShape = it.shape as BoxShape
            val offset = GRID_SIZE / 2
            boxShape.extents = Vector3(offset, 0.001, offset)
            it.translation = Vector3(offset, 0, offset)
            detourNavigationMesh.bakeNavmesh()
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

        ClientDataReceiver.watchFor<TerrainWallsUpdate>()
            .takeUntil(unsub)
            .subscribe { drawWalls(it) }

        CrossScenesManager.positionChangeRequest
            .takeUntil(unsub)
            .subscribe { findPaths(it) }
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

        for (chain in listOf(packet.chains.last())) {
            logger.info { chain.print() }
            val baseChain = variantArrayOf(*Array(chain.size) { chain[it].convert().to3D() })
            val topChain = variantArrayOf(*Array(chain.size) { chain[it].convert().to3D().also { it.y = 0.5 } })
            val sidesChain = VariantArray<Vector3>().also {
                for (i in baseChain.size - 1 downTo 0) {
                    it.pushBack(baseChain[i])
                    it.pushBack(topChain[i])
                }
            }


            val arrayMesh = ArrayMesh().also {
                it.addSurfaceFromArrays(
                    primitive = Mesh.PrimitiveType.PRIMITIVE_TRIANGLE_FAN.id,
                    arrays = surfaceArray(ARRAY_VERTEX = topChain)
                )
                it.addSurfaceFromArrays(
                    primitive = Mesh.PrimitiveType.PRIMITIVE_LINE_STRIP.id,
                    arrays = surfaceArray(ARRAY_VERTEX = topChain)
                )
                it.addSurfaceFromArrays(
                    primitive = Mesh.PrimitiveType.PRIMITIVE_TRIANGLE_STRIP.id,
                    arrays = surfaceArray(ARRAY_VERTEX = sidesChain)
                )
                it.addSurfaceFromArrays(
                    primitive = Mesh.PrimitiveType.PRIMITIVE_LINE_STRIP.id,
                    arrays = surfaceArray(ARRAY_VERTEX = sidesChain)
                )
                it.addSurfaceFromArrays(
                    primitive = Mesh.PrimitiveType.PRIMITIVE_TRIANGLE_FAN.id,
                    arrays = surfaceArray(ARRAY_VERTEX = baseChain)
                )
            }

            val meshInstance = MeshInstance().also {
                it.mesh = arrayMesh
                it.name = "aawall"
                it.setSurfaceMaterial(1, SpatialMaterial().also {
                    it.albedoColor = Color.black
                })
                it.setSurfaceMaterial(3, SpatialMaterial().also {
                    it.albedoColor = Color.black
                })
            }

            StaticBody().also { staticBody ->
                meshInstance.addChild(staticBody)

                CollisionShape().also { collisionShape ->
                    collisionShape.shape = ConvexPolygonShape().also {
                        val arr = PoolVector3Array()
                        topChain.forEach { arr.append(it) }
                        it.points = arr
                    }

                    staticBody.addChild(collisionShape)
                }
            }

            content.addChild(meshInstance)
        }

        detourNavigationMesh.bakeNavmesh()
    }

    override fun _onDestroy() {
        unsub.onNext(true)
    }

    private fun findPaths(promise: Promise<Pair<Vector3, Vector3>, Array<Vector3>>) {
        val (start, end) = promise.data
        promise.resolve(arrayOf(end))
        // fixme: https://github.com/utopia-rise/godot-kotlin-jvm/pull/219
//        val response = detourNavigationMesh.findPath(start, end)
    }
}
