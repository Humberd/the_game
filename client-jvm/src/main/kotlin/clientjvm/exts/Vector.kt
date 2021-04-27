package clientjvm.exts

import godot.core.Vector2
import godot.core.Vector3

fun Vector2.to3D() = Vector3(x, 0, y)
