package main

import ConvexHull
import Finder
import HullPoint
import SampleAreaModifications
import io.map.ObjImporter
import org.recast4j.detour.*
import org.recast4j.detour.io.MeshSetWriter
import org.recast4j.recast.RecastBuilder
import org.recast4j.recast.RecastBuilderConfig
import org.recast4j.recast.RecastConfig
import org.recast4j.recast.RecastConstants
import org.recast4j.recast.geom.InputGeomProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteOrder
import java.util.*


private val m_partitionType = RecastConstants.PartitionType.WATERSHED
private const val m_cellSize = 0.3f
private const val m_cellHeight = 0.2f
private const val m_agentHeight = 2.0f
private const val m_agentRadius = 0.6f
private const val m_agentMaxClimb = 0.9f
private const val m_agentMaxSlope = 45.0f
private const val m_regionMinSize = 8
private const val m_regionMergeSize = 20
private const val m_edgeMaxLen = 12.0f
private const val m_edgeMaxError = 1.3f
private const val m_vertsPerPoly = 6
private const val m_detailSampleDist = 6.0f
private const val m_detailSampleMaxError = 1.0f


fun main() {
    val objData = ObjImporter.load(ObjImporter::class.java.getResourceAsStream("/assets/blender/example-plane.obj")!!)

    val planeVertices = objData.plane.map { HullPoint(it.x.toDouble(), it.z.toDouble()) }
    ConvexHull.makeHull(planeVertices).also { println(it) }

    objData.obstacles.forEach {
        ConvexHull.makeHull(it.map { HullPoint(it.x.toDouble(), it.z.toDouble()) }).also { println(it) }
    }


    val meshData = buildNavMesh(objData.provider)

    val navMesh = NavMesh(meshData, 6, 0)
    val query = NavMeshQuery(navMesh)

    val startPos = floatArrayOf(15.407542f, 0.16500568f, 13.461771f)
    val endPos = floatArrayOf(4.7850227f, 0.16500473f, 5.119936f)

    val finder = Finder(query, navMesh)

    val result = finder.findPos(startPos, endPos)
    val joinToString = result.map { Arrays.toString(it) }.joinToString(",\n")
    println("spos = " + Arrays.toString(startPos))
    println("epos = " + Arrays.toString(endPos))
    println(joinToString)

//    val count = 2
//    val arr = ArrayList<Long>(count)
//    (0 until count).forEach {
//        val measureTimeMillis = measureNanoTime {
//            val result = finder.findPos(startPos, endPos)
//            val joinToString = result.map { Arrays.toString(it) }.joinToString(",\n")
//            println("spos = " + Arrays.toString(startPos))
//            println("epos = " + Arrays.toString(endPos))
//            println(joinToString)
//        }
//        arr.add(measureTimeMillis)
//    }
//
//    println(arr.joinToString("\n"))
//    println("average: ${arr.sum() / arr.size}")


//    result.stream().map(vec -> Arrays.toString(vec)).collect(Collectors.joining(",\n"))

}

fun writeToFile(navMesh: NavMesh) {
    val os = ByteArrayOutputStream()
    val writer = MeshSetWriter()
    writer.write(os, navMesh, ByteOrder.LITTLE_ENDIAN, true)
    val file = File("foo.bin")
    file.createNewFile()
    file.writeBytes(os.toByteArray())
}

fun buildNavMesh(provider: InputGeomProvider): MeshData {
    val rcResult = build(provider)
    val m_pmesh = rcResult.mesh
    for (i in 0 until m_pmesh.npolys) {
        m_pmesh.flags[i] = 1
    }
    val m_dmesh = rcResult.meshDetail
    val params = NavMeshDataCreateParams().also { params ->
        params.verts = m_pmesh.verts
        params.vertCount = m_pmesh.nverts
        params.polys = m_pmesh.polys
        params.polyAreas = m_pmesh.areas
        params.polyFlags = m_pmesh.flags
        params.polyCount = m_pmesh.npolys
        params.nvp = m_pmesh.nvp
        params.detailMeshes = m_dmesh.meshes
        params.detailVerts = m_dmesh.verts
        params.detailVertsCount = m_dmesh.nverts
        params.detailTris = m_dmesh.tris
        params.detailTriCount = m_dmesh.ntris
        params.walkableHeight = m_agentHeight
        params.walkableRadius = m_agentRadius
        params.walkableClimb = m_agentMaxClimb
        params.bmin = m_pmesh.bmin
        params.bmax = m_pmesh.bmax
        params.cs = m_cellSize
        params.ch = m_cellHeight
        params.buildBvTree = true

        params.offMeshConVerts = FloatArray(6)
        params.offMeshConVerts[0] = 0.1f
        params.offMeshConVerts[1] = 0.2f
        params.offMeshConVerts[2] = 0.3f
        params.offMeshConVerts[3] = 0.4f
        params.offMeshConVerts[4] = 0.5f
        params.offMeshConVerts[5] = 0.6f
        params.offMeshConRad = FloatArray(1)
        params.offMeshConRad[0] = 0.1f
        params.offMeshConDir = IntArray(1)
        params.offMeshConDir[0] = 1
        params.offMeshConAreas = IntArray(1)
        params.offMeshConAreas[0] = 2
        params.offMeshConFlags = IntArray(1)
        params.offMeshConFlags[0] = 12
        params.offMeshConUserID = IntArray(1)
        params.offMeshConUserID[0] = 0x4567
        params.offMeshConCount = 1
    }

    return NavMeshBuilder.createNavMeshData(params)
}

fun build(provider: InputGeomProvider): RecastBuilder.RecastBuilderResult {
    val cfg = RecastConfig(
        m_partitionType, m_cellSize, m_cellHeight, m_agentHeight, m_agentRadius,
        m_agentMaxClimb, m_agentMaxSlope, m_regionMinSize, m_regionMergeSize, m_edgeMaxLen, m_edgeMaxError,
        m_vertsPerPoly, m_detailSampleDist, m_detailSampleMaxError, SampleAreaModifications.SAMPLE_AREAMOD_GROUND
    )
    val builderConfig = RecastBuilderConfig(cfg, provider.meshBoundsMin, provider.meshBoundsMax)
    return RecastBuilder().build(provider, builderConfig)
}
