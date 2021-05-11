package clientjvm.exts

import godot.Node
import godot.PackedScene
import godot.global.GD

fun Any.packedScene(): Lazy<PackedScene> = godotLazy {
    val path = this::class.java.canonicalName
        .replace(".Companion", "")
        .replace(".", "/")

    val resourcePath = "res://src/main/kotlin/$path.tscn"

    GD.load(resourcePath)!!
}

fun PackedScene.new(): Node = instance()!!
