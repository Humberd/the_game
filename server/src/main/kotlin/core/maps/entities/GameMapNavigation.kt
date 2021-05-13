package core.maps.entities

import Finder
import SampleAreaModifications
import com.badlogic.gdx.math.Vector2
import org.recast4j.detour.*
import org.recast4j.recast.RecastBuilder
import org.recast4j.recast.RecastBuilderConfig
import org.recast4j.recast.RecastConfig
import org.recast4j.recast.RecastConstants
import org.recast4j.recast.geom.InputGeomProvider

private val m_partitionType = RecastConstants.PartitionType.WATERSHED
private const val m_cellSize = 0.1f
private const val m_cellHeight = 0.2f
private const val m_agentHeight = 2.0f
private const val m_agentRadius = 0.5f
private const val m_agentMaxClimb = 0.1f
private const val m_agentMaxSlope = 10.0f
private const val m_regionMinSize = 8
private const val m_regionMergeSize = 20
private const val m_edgeMaxLen = 12.0f
private const val m_edgeMaxError = 1.3f
private const val m_vertsPerPoly = 6
private const val m_detailSampleDist = 6.0f
private const val m_detailSampleMaxError = 1.0f

class GameMapNavigation {
    lateinit var navMesh: NavMesh
    lateinit var query: NavMeshQuery

    fun onInit(provider: InputGeomProvider) {
        navMesh = NavMesh(buildNavMesh(provider), 6, 0)
        query = NavMeshQuery(navMesh)
    }

    fun findPath(start: Vector2, end: Vector2): List<Vector2> {
        val finder = Finder(query, navMesh)
        val result: List<FloatArray> =
            finder.findPos(floatArrayOf(start.x, 0.16f, start.y), floatArrayOf(end.x, 0.16f, end.y))

        return result.map { Vector2(it[0], it[2]) }
    }

    private fun buildNavMesh(provider: InputGeomProvider): MeshData {
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
        }

        return NavMeshBuilder.createNavMeshData(params)
    }

    private fun build(provider: InputGeomProvider): RecastBuilder.RecastBuilderResult {
        val cfg = RecastConfig(
            m_partitionType, m_cellSize, m_cellHeight, m_agentHeight, m_agentRadius,
            m_agentMaxClimb, m_agentMaxSlope, m_regionMinSize, m_regionMergeSize, m_edgeMaxLen, m_edgeMaxError,
            m_vertsPerPoly, m_detailSampleDist, m_detailSampleMaxError, SampleAreaModifications.SAMPLE_AREAMOD_GROUND
        )
        val builderConfig = RecastBuilderConfig(cfg, provider.meshBoundsMin, provider.meshBoundsMax)
        return RecastBuilder().build(provider, builderConfig)
    }

}
