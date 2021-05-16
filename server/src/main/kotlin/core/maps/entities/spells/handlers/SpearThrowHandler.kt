package core.maps.entities.spells.handlers

import core.maps.entities.GameContext
import core.maps.entities.creatures.Creature
import core.maps.entities.spells.SpellHandler
import core.types.WorldPosition
import mu.KLogging
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class SpearThrowHandler(
    private val caster: Creature,
    private val context: GameContext
) : SpellHandler() {
    companion object : KLogging()

    private val fullChargeDuration = Duration.Companion.milliseconds(1000)

    private var castStartTime: TimeMark? = null

    override fun onCastStart(targetPosition: WorldPosition) {
        check(castStartTime == null)
        castStartTime = TimeSource.Monotonic.markNow()
        logger.info { "Spear throw start" }
    }

    override fun onCastEnd(targetPosition: WorldPosition) {
        val _castStartTime = castStartTime
        check(_castStartTime != null)
        val elapsedTime = _castStartTime.elapsedNow()
        val elapsedRatio = minOf(elapsedTime / fullChargeDuration, 1.0)
        logger.info { "Spear throw end after $elapsedTime with ratio $elapsedRatio" }
        castStartTime = null
    }
}
