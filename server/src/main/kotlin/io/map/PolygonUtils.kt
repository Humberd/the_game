package io.map

import com.badlogic.gdx.math.ConvexHull
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import de.lighti.clipper.*

object PolygonUtils {
    const val precision = 1_000_000L

    fun convert3dPolygonTo2d(polygon: List<Vector3>): Array<Vector2> {
        val vertices = FloatArray(polygon.size * 2) {
            if (it % 2 == 0) {
                polygon[it / 2].x
            } else {
                polygon[it / 2].z
            }
        }

        val result = ConvexHull().computePolygon(vertices, false)
        return convertFloatArrayToVectorArray(result.toArray())
    }

    fun convertFloatArrayToVectorArray(arr: FloatArray): Array<Vector2> {
        return Array(arr.size / 2) {
            Vector2(arr[it * 2], arr[it * 2 + 1])
        }
    }

    fun convertFloatPolygonToLong(polygon: Array<Vector2>): List<Point.LongPoint> {
        return polygon.map { Point.LongPoint((it.x * precision).toLong(), (it.y * precision).toLong()) }
    }

    fun convertLongPolygonToFloat(polygon: List<Point.LongPoint>): List<Vector2> {
        return polygon.map { Vector2(it.x / precision.toFloat(), it.y / precision.toFloat()) }
    }

    fun findPathsFor(x: Int, y: Int, obstacles: Paths): List<List<Vector2>> {
        val solution = Paths()
        val clipper = DefaultClipper(Clipper.STRICTLY_SIMPLE)
        clipper.addPaths(obstacles, Clipper.PolyType.SUBJECT, true)
        val bound = arrayOf(
            Vector2(x.toFloat(), y.toFloat()),
            Vector2(x.toFloat(), y.toFloat() + 1),
            Vector2(x.toFloat() + 1, y.toFloat() + 1),
            Vector2(x.toFloat() + 1, y.toFloat()),
            Vector2(x.toFloat(), y.toFloat())
        )
        clipper.addPath(
            Path().also { it.addAll(convertFloatPolygonToLong(bound)) },
            Clipper.PolyType.CLIP,
            true
        )

        val hasResults = clipper.execute(Clipper.ClipType.INTERSECTION, solution)
        if (!hasResults) {
            throw Error("something wrong went with clipping")
        }

        return solution.map { convertLongPolygonToFloat(it) }
    }
}
