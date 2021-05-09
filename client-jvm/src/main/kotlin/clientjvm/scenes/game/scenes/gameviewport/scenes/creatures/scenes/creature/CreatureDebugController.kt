package clientjvm.scenes.game.scenes.gameviewport.scenes.creatures.scenes.creature

import clientjvm.exts.convert
import clientjvm.exts.print
import clientjvm.exts.surfaceArray
import clientjvm.exts.to3D
import godot.*
import godot.annotation.RegisterClass
import godot.core.Vector3
import godot.core.variantArrayOf
import mu.KLogging
import pl.humberd.models.GameMaths

@RegisterClass
class CreatureDebugController : Spatial() {
    companion object : KLogging()

    private data class StatRadius(
        val name: String,
        val radius: Float,
        val node: Node
    )

    private val stats = hashMapOf<String, StatRadius>()

    fun displayStat(name: String, radius: Float) {
        val existingStat = stats[name]
        if (existingStat != null) {
            if (existingStat.radius == radius) {
                return
            }
            existingStat.node.queueFree()
            stats.remove(name)
        }

        logger.info { "$name,$radius" }
        logger.info { GameMaths.generateCircle(radius).contentToString() }
        val chain = GameMaths.generateCircle(radius).map { it.convert().to3D() }.toTypedArray()
        val variantChain = variantArrayOf(*chain)
        logger.info { variantChain.print() }

        val arrayMesh = ArrayMesh().also {
            it.addSurfaceFromArrays(
                primitive = Mesh.PrimitiveType.PRIMITIVE_LINE_LOOP.id,
                arrays = surfaceArray(ARRAY_VERTEX = variantChain)
            )
        }

        val meshInstance = MeshInstance().also {
            it.mesh = arrayMesh
            it.name = name
            it.translation = Vector3(0f, 0.1f, 0f)
        }

        val statRadius = StatRadius(
            name, radius, meshInstance
        )
        stats[name] = statRadius
        addChild(meshInstance)
    }
}
