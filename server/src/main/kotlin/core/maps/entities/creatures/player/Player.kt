package core.maps.entities.creatures.player

import core.StateChangeNotifier
import core.maps.entities.CollisionCategory
import core.maps.entities.GameMap
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.CreatureSeed
import core.types.SpriteId
import pl.humberd.models.Milliseconds
import pl.humberd.models.PID
import pl.humberd.models.SID

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

data class PlayerSeed(
    val pid: PID,
    val spellsContainer: SpellsContainer
)

class Player(
    creatureSeed: CreatureSeed,
    gameMap: GameMap,
    notifier: StateChangeNotifier,
    playerSeed: PlayerSeed
) : Creature(creatureSeed, gameMap, notifier) {
    val pid = playerSeed.pid
    val spellsContainer = playerSeed.spellsContainer

    override val hooks = PlayerHooks(this, notifier)
    override val collisionCategory = CollisionCategory.PLAYER

    override fun toString(): String {
        return "Player(${pid})"
    }
}
