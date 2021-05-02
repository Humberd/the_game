package clientjvm.addons.godotnavigationlite

import godot.Spatial

class DetourNavigationMesh(private val node: Spatial) {
    fun bakeNavmesh() {
        node.call("bake_navmesh")
    }

    fun clearNavmesh() {
        node.call("clear_navmesh")
    }
}
