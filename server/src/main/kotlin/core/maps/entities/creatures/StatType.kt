package core.maps.entities.creatures

import core.types.Experience
import core.types.Level

enum class StatType {
    DEFENSE {
        override fun calculateBase(level: Level): Int {
            return level.value * 2
        }

        override fun getStatFor(creature: Creature): IntStatValue {
            return creature.stats.defense
        }
    },
    ATTACK {
        override fun calculateBase(level: Level): Int {
            return level.value * 2
        }

        override fun getStatFor(creature: Creature): IntStatValue {
            return creature.stats.attack
        }
    },
    ATTACK_SPEED {
        override fun calculateBase(level: Level): Float {
            return (0.5 + level.value * 0.05).toFloat()
        }

        override fun getStatFor(creature: Creature): FloatStatValue {
            return creature.stats.attackSpeed
        }
    },
    MOVEMENT_SPEED {
        override fun calculateBase(level: Level): Float {
            return (1 + level.value * 0.05).toFloat()
        }

        override fun getStatFor(creature: Creature): FloatStatValue {
            return creature.stats.movementSpeed
        }
    },
    HEALTH_POOL {
        override fun calculateBase(level: Level): Int {
            return level.value * 15
        }

        override fun getStatFor(creature: Creature): IntStatValue {
            return creature.stats.healthPool
        }
    };

    fun calculateBaseValueFor(experience: Experience): Number {
        return calculateBase(experience.toLevel())
    }

    protected abstract fun calculateBase(level: Level): Number
    abstract fun getStatFor(creature: Creature): StatValue<out Number>

}
