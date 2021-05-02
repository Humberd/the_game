package clientjvm.addons.godotnavigationlite

import godot.Spatial
import godot.core.Vector3

class DetourNavigationMesh(private val node: Spatial) {
    fun findPath(start: Vector3, end: Vector3): Any? {
        return node.call("find_path", start, end)
    }

    fun bakeNavmesh() {
        node.call("bake_navmesh")
    }

    fun clearNavmesh() {
        node.call("clear_navmesh")
    }
}
