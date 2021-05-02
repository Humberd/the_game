package clientjvm.global

import clientjvm.exts.Promise
import clientjvm.exts.emitter
import godot.core.Vector3

object CrossScenesManager {
    val positionChangeRequest by emitter<Promise<Pair<Vector3, Vector3>, Array<Vector3>>>()
}
