package core.maps.entities.creatures

import core.types.Experience

abstract class StatValue<T : Number>(
    protected val statType: StatType,
    base: T,
    current: T
) {
    var base: T = base
        protected set
    var current: T = current
        protected set

    abstract fun updateBase(experience: Experience)
    fun updateCurrent() {
        current = base
    }
}

class IntStatValue(statType: StatType) : StatValue<Int>(statType, 0, 0) {
    override fun updateBase(experience: Experience) {
        val newBase = statType.calculateBaseValueFor(experience)
        check(newBase is Int)
        base = newBase
    }
}

class FloatStatValue(statType: StatType) : StatValue<Float>(statType, 0f, 0f) {
    override fun updateBase(experience: Experience) {
        val newBase = statType.calculateBaseValueFor(experience)
        check(newBase is Float)
        base = newBase
    }
}
