package clientjvm.exts

import godot.Camera
import godot.Node
import godot.Spatial
import godot.core.RID
import godot.core.Vector3

val Node.currentCamera: Camera
    get() = getViewport()?.getCamera()!!

data class RayCastResult(
    val collider: Node,
    val collider_id: Long,
    val normal: Vector3,
    val position: Vector3,
    val rid: RID,
    val shape: Long
) {
    override fun toString(): String {
        return "\nRayCastResult(\n\tcollider=$collider,\n\tcollider_id=$collider_id,\n\tnormal=$normal,\n\tposition=$position,\n\trid=$rid,\n\tshape=$shape\n)"
    }
}

fun Spatial.castCameraRays(
    collideWithAreas: Boolean = false,
    collideWithBodies: Boolean = false,
    layer: Long = 2147483647
): RayCastResult? {
    val rayLength = 1000
    val mousePosition = getViewport()?.getMousePosition()!!
    val from = currentCamera.projectRayOrigin(mousePosition)
    val to = from + currentCamera.projectRayNormal(mousePosition) * rayLength
    val spaceState = getWorld()?.directSpaceState!!

    val result =
        spaceState.intersectRay(
            from,
            to,
            collideWithAreas = collideWithAreas,
            collideWithBodies = collideWithBodies,
            collisionMask = layer
        )
    if (result.size == 0) {
        return null
    }

    return RayCastResult(
        collider = result["collider"] as Node,
        collider_id = result["collider_id"] as Long,
        normal = result["normal"] as Vector3,
        position = result["position"] as Vector3,
        rid = result["rid"] as RID,
        shape = result["shape"] as Long
    )
}
