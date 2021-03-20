package core.maps.entities.creatures

import core.types.Experience
import core.types.Level

enum class StatType {
    DEFENSE {
        override fun calculateBase(level: Level): Int {
            return level.value * 2
        }
    },
    ATTACK {
        override fun calculateBase(level: Level): Int {
            return level.value * 2
        }
    },
    ATTACK_SPEED {
        override fun calculateBase(level: Level): Float {
            return (0.5 + level.value * 0.05).toFloat()
        }
    },
    MOVEMENT_SPEED {
        override fun calculateBase(level: Level): Float {
            return (1 + level.value * 0.05).toFloat()
        }
    },
    HEALTH_POOL {
        override fun calculateBase(level: Level): Int {
            return level.value * 15
        }
    };

    fun calculateBaseValueFor(experience: Experience): Number {
        return calculateBase(experience.toLevel())
    }

    protected abstract fun calculateBase(level: Level): Number

}
