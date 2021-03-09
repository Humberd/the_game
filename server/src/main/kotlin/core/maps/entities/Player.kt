package core.maps.entities

import core.types.*
import org.mini2Dx.gdx.math.Vector2
import utils.Milliseconds

data class Spell(
    val sid: SID,
    val name: String,
    val spriteId: SpriteId,
    val cooldown: Milliseconds
)

class SpellsContainer(
    var spell1: Spell? = null,
    var spell2: Spell? = null,
    var spell3: Spell? = null,
    var spell4: Spell? = null
) {
    fun getSpell(sid: SID): Spell {
        return if (spell1?.sid == sid) {
            spell1!!
        } else if (spell2?.sid == sid) {
            spell2!!
        } else if (spell3?.sid == sid) {
            spell3!!
        } else if (spell4?.sid == sid) {
            spell4!!
        } else {
            throw Error("Spell not found for ${sid}")
        }
    }
}

class Player(
    val pid: PID,
    cid: CID,
    name: CreatureName,
    health: UInt,
    spriteId: SpriteId,
    position: WorldPosition = Vector2(0f, 0f),
    val spellsContainer: SpellsContainer
) : Creature(cid, name, health, spriteId, position) {

    override fun toString(): String {
        return pid.toString()
    }
}


//abstract class Base(
//    private val baseName: String
//) {
//    open val child = Child()
//
//    open inner class Child {
//        fun compute() = baseName
//    }
//}
//
//class Car(
//    private val name: String
//) : Base("Hello") {
//    override val child = Mercedes()
//
//    inner class Mercedes : Base.Child() {
//        fun getName() = name
//    }
//}
//
//val car = Car("BMW")
//val compute = println(car.child.compute())
//val name = println(car.child.getName())
