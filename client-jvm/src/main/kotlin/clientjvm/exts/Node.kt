package clientjvm.exts

import godot.Camera
import godot.Node

val Node.currentCamera: Camera
    get() = getViewport()?.getCamera()!!
