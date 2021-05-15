package core.maps.entities

import box2d.Box2DDebugRenderer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import org.recast4j.detour.MeshTile
import org.recast4j.detour.NavMesh

class Renderer(private val navMesh: NavMesh) : Box2DDebugRenderer() {
    companion object {
        val NAVMESH_LINE = Color.valueOf("#76BAF7")
        val NAVMESH_POINT = Color.valueOf("#5EA7F7")
    }

    override fun render(world: World?, projMatrix: Matrix4?) {
        super.render(world, projMatrix)

        repeat(navMesh.maxTiles) {
            drawTile(navMesh.getTile(it))
        }
    }

    private fun drawTile(tile: MeshTile) {
        val vertsPool = tile.data.verts
        renderer.begin(ShapeRenderer.ShapeType.Point)
        renderer.color = NAVMESH_POINT
        repeat(tile.data.header.vertCount) {
            val x = vertsPool.getX(it)
            val z = vertsPool.getZ(it)
            renderer.point(x, z, 0f)
        }
        renderer.end()

        renderer.begin(ShapeRenderer.ShapeType.Line)
        tile.data.polys.forEach { poly ->
            val arr = Array(poly.vertCount) {
                Vector2(vertsPool.getX(poly.verts[it]), vertsPool.getZ(poly.verts[it]))
            }
            drawSolidPolygon(arr, poly.vertCount, NAVMESH_LINE, true)
        }

        renderer.end()
    }
}

private fun FloatArray.getX(i: Int): Float {
    return this[i * 3]
}

private fun FloatArray.getY(i: Int): Float {
    return this[i * 3 + 1]
}

private fun FloatArray.getZ(i: Int): Float {
    return this[i * 3 + 2]
}
