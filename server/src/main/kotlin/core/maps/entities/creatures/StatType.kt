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
            return (level.value * 1.2).toFloat()
        }
    },
    MOVEMENT_SPEED {
        override fun calculateBase(level: Level): Float {
            return (level.value * 0.05 + 1).toFloat()
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
