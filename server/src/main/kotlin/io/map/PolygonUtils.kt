package io.map

import com.badlogic.gdx.math.ConvexHull
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import de.lighti.clipper.Point

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
        return Array(result.size / 2) {
            Vector2(result[it * 2], result[it * 2 + 1])
        }
    }

    fun convertFloatPolygonToLong(polygon: Array<Vector2>): List<Point.LongPoint> {
        return polygon.map { Point.LongPoint((it.x * precision).toLong(), (it.y * precision).toLong()) }
    }

    fun convertLongPolygonToFloat(polygon: List<Point.LongPoint>): Array<Vector2> {
        return polygon.map { Vector2(it.x / precision.toFloat(), it.y / precision.toFloat()) }.toTypedArray()
    }
}
