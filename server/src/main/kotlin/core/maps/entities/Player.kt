package core.maps.entities

import core.StateChangeNotifier
import core.types.PID
import core.types.SID
import core.types.SpriteId
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

    val hooks = PlayerHooks(this, notifier)

    override fun toString(): String {
        return pid.toString()
    }

    override fun onOtherCreatureDisappearFromViewRange(otherCreature: Creature) {
        notifier.notifyCreatureDisappear(pid, otherCreature)
    }

    override fun onOtherCreatureAppearInViewRange(otherCreature: Creature) {
        notifier.notifyCreatureUpdate(pid, otherCreature)
    }

    override fun onOtherCreaturePositionChange(otherCreature: Creature) {
        notifier.notifyCreaturePositionUpdate(pid, otherCreature)
    }
}
