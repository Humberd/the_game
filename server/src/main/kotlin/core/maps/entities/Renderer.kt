package core.maps.entities

import box2d.Box2DDebugRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.box2d.World
import org.recast4j.detour.MeshTile
import org.recast4j.detour.NavMesh

class Renderer(private val navMesh: NavMesh) : Box2DDebugRenderer() {
    init {
        for (i in 0 until navMesh.maxTiles) {
            val tile = navMesh.getTile(i)
            if (tile != null && tile.data != null) {
                drawTile(tile)
            }
        }
    }

    override fun render(world: World?, projMatrix: Matrix4?) {
        super.render(world, projMatrix)

        drawTile(navMesh.getTile(0))
    }

    private fun drawTile(tile: MeshTile) {
        val vertsPool = tile.data.verts
        renderer.begin(ShapeRenderer.ShapeType.Point)
//        renderer.color = Color.NAVY
        repeat(tile.data.header.vertCount) {
            val x = vertsPool.getX(it)
            val z = vertsPool.getZ(it)
            renderer.point(x, z, 0f)
        }
        renderer.end()
    }
}

fun FloatArray.getX(i: Int): Float {
    return this[i * 3]
}

fun FloatArray.getY(i: Int): Float {
    return this[i * 3 + 1]
}

fun FloatArray.getZ(i: Int): Float {
    return this[i * 3 + 2]
}
