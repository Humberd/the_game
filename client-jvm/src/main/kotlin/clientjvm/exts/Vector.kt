package clientjvm.exts

import godot.core.Vector2
import godot.core.Vector3
import pl.humberd.models.ApiVector2

fun Vector2.to3D() = Vector3(x, 0, y)

fun ApiVector2.convert() = Vector2(x, y)
