/*
recast4j copyright (c) 2021 Piotr Piastucki piotr@jtilia.org

This software is provided 'as-is', without any express or implied
warranty.  In no event will the authors be held liable for any damages
arising from the use of this software.
Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:
1. The origin of this software must not be misrepresented; you must not
 claim that you wrote the original software. If you use this software
 in a product, an acknowledgment in the product documentation would be
 appreciated but is not required.
2. Altered source versions must be plainly marked as such, and must not be
 misrepresented as being the original software.
3. This notice may not be removed or altered from any source distribution.

Version modified
*/

package io.map

import com.badlogic.gdx.math.Vector3
import org.recast4j.recast.geom.InputGeomProvider
import org.recast4j.recast.geom.SimpleInputGeomProvider
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object ObjImporter {
    private class ObjImporterContext {
        var vertexPositions: MutableList<Float> = ArrayList()
        var meshFaces: MutableList<Int> = ArrayList()

        val obstacles: MutableList<Obstacle> = ArrayList()
        lateinit var currentObstacle: Obstacle

        fun addObstacle(obstacle: Obstacle) {
            obstacles.add(obstacle)
            currentObstacle = obstacle
        }
    }

    internal data class Obstacle(
        val name: String,
        val vertices: MutableList<Vector3> = ArrayList()
    )

    data class ObjResult(
        val provider: InputGeomProvider,
        val plane: List<Vector3>,
        val obstacles: List<List<Vector3>>
    )

    fun load(inputStream: InputStream): ObjResult {
        val context = ObjImporterContext()
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.forEachLine {
            val line = it.trim { it <= ' ' }
            readLine(line, context)
        }

        val plane = context.obstacles.find { it.name == "Plane" }
        if (plane == null) {
            throw Error("Plane not found")
        }

        return ObjResult(
            provider = SimpleInputGeomProvider(context.vertexPositions, context.meshFaces),
            plane = plane.vertices,
            obstacles = context.obstacles.filter { it.name != "Plane" }.map { it.vertices }
        )
    }

    private fun readLine(line: String, context: ObjImporterContext) {
        if (line.startsWith("v")) {
            readVertex(line, context)
        } else if (line.startsWith("f")) {
            readFace(line, context)
        } else if (line.startsWith("o")) {
            readObject(line, context)
        }
    }

    private fun readObject(line: String, context: ObjImporterContext) {
        val name = line.removeRange(0..1)
        context.addObstacle(Obstacle(name))
    }

    private fun readVertex(line: String, context: ObjImporterContext) {
        if (line.startsWith("v ")) {
            val vert = readVector3f(line)
            for (vp in vert) {
                context.vertexPositions.add(vp)
            }
            context.currentObstacle.vertices.add(Vector3(vert[0], vert[1], vert[2]))
        }
    }

    private fun readVector3f(line: String): FloatArray {
        val v = line.split(" ").toTypedArray()
        if (v.size < 4) {
            throw RuntimeException("Invalid vector, expected 3 coordinates, found " + (v.size - 1))
        }
        return floatArrayOf(v[1].toFloat(), v[2].toFloat(), v[3].toFloat())
    }

    private fun readFace(line: String, context: ObjImporterContext) {
        val v = line.split(" ").toTypedArray()
        if (v.size < 4) {
            throw RuntimeException("Invalid number of face vertices: 3 coordinates expected, found " + v.size)
        }
        for (j in 0 until v.size - 3) {
            context.meshFaces.add(readFaceVertex(v[1], context))
            for (i in 0..1) {
                context.meshFaces.add(readFaceVertex(v[2 + j + i], context))
            }
        }
    }

    private fun readFaceVertex(face: String, context: ObjImporterContext): Int {
        val v = face.split("/").toTypedArray()
        return getIndex(v[0].toInt(), context.vertexPositions.size)
    }

    private fun getIndex(posi: Int, size: Int): Int {
        var posi = posi
        if (posi > 0) {
            posi--
        } else if (posi < 0) {
            posi = size + posi
        } else {
            throw RuntimeException("0 vertex index")
        }
        return posi
    }
}
