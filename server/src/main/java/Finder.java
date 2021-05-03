import org.recast4j.detour.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.recast4j.detour.DetourCommon.*;

public class Finder {
    NavMeshQuery m_navQuery;
    NavMesh m_navMesh;
    float[] m_polyPickExt = new float[]{2, 4, 2};
    DefaultQueryFilter m_filter = new DefaultQueryFilter(15, 0, new float[]{1f, 1f, 1f, 1f, 2f, 1.5f});
    int MAX_SMOOTH = 2048;

    public Finder(NavMeshQuery m_navQuery, NavMesh m_navMesh) {
        this.m_navQuery = m_navQuery;
        this.m_navMesh = m_navMesh;
    }

    public List<float[]> findPos(float[] m_spos, float[] m_epos) {
        List<Long> m_polys;
        List<float[]> m_smoothPath = null;
        long m_startRef = m_navQuery.findNearestPoly(m_spos, m_polyPickExt, m_filter).result.getNearestRef();
        long m_endRef = m_navQuery.findNearestPoly(m_epos, m_polyPickExt, m_filter).result.getNearestRef();

        m_polys = m_navQuery.findPath(m_startRef, m_endRef, m_spos, m_epos, m_filter,
                NavMeshQuery.DT_FINDPATH_ANY_ANGLE, Float.MAX_VALUE).result;
        if (!m_polys.isEmpty()) {
            List<Long> polys = new ArrayList<>(m_polys);
            // Iterate over the path to find smooth path on the detail mesh surface.
            float[] iterPos = m_navQuery.closestPointOnPoly(m_startRef, m_spos).result.getClosest();
            float[] targetPos = m_navQuery.closestPointOnPoly(polys.get(polys.size() - 1), m_epos).result
                    .getClosest();

            float STEP_SIZE = 0.5f;
            float SLOP = 0.01f;

            m_smoothPath = new ArrayList<>();
            m_smoothPath.add(iterPos);

            // Move towards target a small advancement at a time until target reached or
            // when ran out of memory to store the path.
            while (!polys.isEmpty() && m_smoothPath.size() < MAX_SMOOTH) {
                // Find location to steer towards.
                Optional<PathUtils.SteerTarget> steerTarget = PathUtils.getSteerTarget(m_navQuery, iterPos, targetPos,
                        SLOP, polys);
                if (!steerTarget.isPresent()) {
                    break;
                }
                boolean endOfPath = (steerTarget.get().steerPosFlag & NavMeshQuery.DT_STRAIGHTPATH_END) != 0
                        ? true
                        : false;
                boolean offMeshConnection = (steerTarget.get().steerPosFlag
                        & NavMeshQuery.DT_STRAIGHTPATH_OFFMESH_CONNECTION) != 0 ? true : false;

                // Find movement delta.
                float[] delta = vSub(steerTarget.get().steerPos, iterPos);
                float len = (float) Math.sqrt(DemoMath.vDot(delta, delta));
                // If the steer target is end of path or off-mesh link, do not move past the location.
                if ((endOfPath || offMeshConnection) && len < STEP_SIZE) {
                    len = 1;
                } else {
                    len = STEP_SIZE / len;
                }
                float[] moveTgt = vMad(iterPos, delta, len);
                // Move
                Result<MoveAlongSurfaceResult> result = m_navQuery.moveAlongSurface(polys.get(0), iterPos,
                        moveTgt, m_filter);
                MoveAlongSurfaceResult moveAlongSurface = result.result;

                iterPos = new float[3];
                iterPos[0] = moveAlongSurface.getResultPos()[0];
                iterPos[1] = moveAlongSurface.getResultPos()[1];
                iterPos[2] = moveAlongSurface.getResultPos()[2];

                List<Long> visited = result.result.getVisited();
                polys = PathUtils.fixupCorridor(polys, visited);
                polys = PathUtils.fixupShortcuts(polys, m_navQuery);

                Result<Float> polyHeight = m_navQuery.getPolyHeight(polys.get(0), moveAlongSurface.getResultPos());
                if (polyHeight.succeeded()) {
                    iterPos[1] = polyHeight.result;
                }

                // Handle end of path and off-mesh links when close enough.
                if (endOfPath && PathUtils.inRange(iterPos, steerTarget.get().steerPos, SLOP, 1.0f)) {
                    // Reached end of path.
                    vCopy(iterPos, targetPos);
                    if (m_smoothPath.size() < MAX_SMOOTH) {
                        m_smoothPath.add(iterPos);
                    }
                    break;
                } else if (offMeshConnection
                        && PathUtils.inRange(iterPos, steerTarget.get().steerPos, SLOP, 1.0f)) {
                    // Reached off-mesh connection.
                    // Advance the path up to and over the off-mesh connection.
                    long prevRef = 0;
                    long polyRef = polys.get(0);
                    int npos = 0;
                    while (npos < polys.size() && polyRef != steerTarget.get().steerPosRef) {
                        prevRef = polyRef;
                        polyRef = polys.get(npos);
                        npos++;
                    }
                    polys = polys.subList(npos, polys.size());

                    // Handle the connection.
                    Result<Tupple2<float[], float[]>> offMeshCon = m_navMesh
                            .getOffMeshConnectionPolyEndPoints(prevRef, polyRef);
                    if (offMeshCon.succeeded()) {
                        float[] startPos = offMeshCon.result.first;
                        float[] endPos = offMeshCon.result.second;
                        if (m_smoothPath.size() < MAX_SMOOTH) {
                            m_smoothPath.add(startPos);
                            // Hack to make the dotted path not visible during off-mesh connection.
                            if ((m_smoothPath.size() & 1) != 0) {
                                m_smoothPath.add(startPos);
                            }
                        }
                        // Move position at the other side of the off-mesh link.
                        vCopy(iterPos, endPos);
                        iterPos[1] = m_navQuery.getPolyHeight(polys.get(0), iterPos).result;
                    }
                }

                // Store results.
                if (m_smoothPath.size() < MAX_SMOOTH) {
                    m_smoothPath.add(iterPos);
                }
            }
        }

        return m_smoothPath;
    }
}
