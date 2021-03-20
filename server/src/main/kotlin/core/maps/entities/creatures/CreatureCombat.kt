package core.maps.entities.creatures

import core.AsyncGameTask
import core.GameLoop
import core.maps.entities.creatures.player.Player
import core.types.SpriteId
import infrastructure.udp.egress.EgressDataPacket
import utils.getDistance
import utils.ms
import utils.sec

class CreatureCombat(private val creature: Creature) {
    var attackedTarget: Creature? = null
        private set
    val attackedByTargets: MutableSet<Creature> = mutableSetOf()
    private var attackTask: AsyncGameTask? = null

    fun takeDamage(damage: UInt) {
        val newHealth = creature.stats.healthCurrent - damage.toInt()

        if (newHealth <= 0) {
            creature.stats.healthCurrent = 0
            die()
        } else {
            creature.stats.healthCurrent = newHealth
        }

        creature.hooks.onSelfDamageTaken(damage)
        creature.creaturesThatSeeMe.forEach { it.hooks.onOtherCreatureDamageTaken(creature, damage) }
    }

    private fun die() {
        attackedByTargets.toTypedArray().forEach { it.combat.stopAttacking() }
        creature.hooks.onDeath()
    }

    fun startAttacking(target: Creature) {
        if (isCurrentlyAttacking()) {
            if (target === attackedTarget) {
                throw IllegalStateException("Cannot attack the same target again")
            }

            stopAttacking()
        }

        if (!creature.canSee(target)) {
            throw Error("Creature can't see the target")
        }

        attackedTarget = target
        target.combat.attackedByTargets.add(creature)

        val attackSpeed = 1000.ms
        val damage = 10u

        val projectileUnitsPerSecond = 3f

        attackTask = GameLoop.instance.requestAsyncTask(attackSpeed) {
            val distanceToTarget = getDistance(creature.position, target.position)
            val projectileDelay = (distanceToTarget.toFloat() / projectileUnitsPerSecond).sec

            // FIXME: 16.03.2021 Should be item hook: `onItemUsed` or something like that
            if (creature is Player) {
                creature.notifier.sendProjectile(
                    creature.pid, EgressDataPacket.ProjectileSend(
                        spriteId = SpriteId(13u),
                        sourcePosition = creature.position,
                        targetPosition = target.position,
                        duration = projectileDelay
                    )
                )
            }
            creature.creaturesThatSeeMe
                .forEach {
                    if (it is Player) {
                        creature.notifier.sendProjectile(
                            it.pid, EgressDataPacket.ProjectileSend(
                                spriteId = SpriteId(13u),
                                sourcePosition = creature.position,
                                targetPosition = target.position,
                                duration = projectileDelay
                            )
                        )
                    }
                }

            GameLoop.instance.requestAsyncTaskOnce(projectileDelay) {
                target.combat.takeDamage(damage)
            }
        }

        creature.hooks.onStartAttackOtherCreature(target)
        target.hooks.onBeingAttackedBy(creature)
    }

    fun stopAttacking() {
        val task = attackTask
        check(task != null)
        val target = attackedTarget
        check(target != null)

        task.cancel()
        attackedTarget = null
        target.combat.attackedByTargets.remove(creature)
        attackTask = null

        creature.hooks.onStoppedAttackOtherCreature(target)
    }

    fun isCurrentlyAttacking(): Boolean {
        return attackedTarget != null
    }
}
