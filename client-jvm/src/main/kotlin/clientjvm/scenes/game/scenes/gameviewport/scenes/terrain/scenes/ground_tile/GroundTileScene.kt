package clientjvm.scenes.game.scenes.gameviewport.scenes.terrain.scenes.ground_tile

import clientjvm.exts.convert
import clientjvm.exts.packedScene
import clientjvm.exts.surfaceArray
import clientjvm.exts.to3D
import clientjvm.global.AssetLoader
import godot.*
import godot.annotation.RegisterClass
import godot.core.*
import pl.humberd.models.ApiVector2

@RegisterClass
class GroundTileScene : Sprite3D() {
    private val walls = ArrayList<MeshInstance>()

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

    fun drawWalls(chains: Array<Array<ApiVector2>>, terrainScene: Node) {
        for (chain in chains) {
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
                        baseChain.forEach { arr.append(it) }
                        topChain.forEach { arr.append(it) }
                        it.points = arr
                    }

                    staticBody.addChild(collisionShape)
                }
            }

            walls.add(meshInstance)
            terrainScene.addChild(meshInstance)
        }
    }

    fun destroyWalls() {
        walls.forEach { it.queueFree() }
        walls.clear()
    }
}


