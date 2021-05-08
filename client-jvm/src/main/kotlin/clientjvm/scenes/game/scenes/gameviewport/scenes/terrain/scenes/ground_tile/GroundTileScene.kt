package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile

import clientjvm.exts.*
import clientjvm.global.AssetLoader
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.*
import pl.humberd.models.ApiVector2

@RegisterClass
class GroundTileScene : Spatial() {
    private lateinit var tile: Sprite3D
    private lateinit var obstacles: Spatial

    companion object {
        val packedScene by packedScene()
    }

    @RegisterFunction
    override fun _ready() {
        tile = getNodeAs("Tile")
        obstacles = getNodeAs("Obstacles")
        unsetTile()
    }

    fun load(gridCoordinates: Vector2) {
        translate(gridCoordinates.to3D())
        name = "Tile(${gridCoordinates.x}, ${gridCoordinates.y})"
    }

    fun setTile(spriteId: UShort) {
        tile.texture = AssetLoader.loadSprite(spriteId)
    }

    fun unsetTile() {
        tile.texture = null
    }

    fun drawObstacles(chains: Array<Array<ApiVector2>>) {
        for (chain in chains) {
            val baseChain = variantArrayOf(*Array(chain.size) { chain[it].convert().to3D() - translation })
            val topChain =
                variantArrayOf(*Array(chain.size) { chain[it].convert().to3D().also { it.y = 0.5 } - translation })
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
                it.name = "obstacle"
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
                        baseChain.forEach { arr.append(it) }
                        topChain.forEach { arr.append(it) }
                        it.points = arr
                    }

                    staticBody.addChild(collisionShape)
                }
            }

            obstacles.addChild(meshInstance)
        }
    }

    fun destroyObstacles() {
        obstacles.getChildren().forEach {
            require(it is Node)
            it.queueFree()
        }
    }
}


