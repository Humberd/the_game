package core.maps.entities.creatures

import core.maps.entities.items.ModificationItem
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

    protected val modificationItems = hashSetOf<ModificationItem>()

    abstract fun recalculateBase(experience: Experience)
    abstract fun recalculateCurrent()

    fun addModification(item: ModificationItem) {
        require(item.statType == statType)
        check(!modificationItems.contains(item))
        modificationItems.add(item)
    }

    fun removeModification(item: ModificationItem) {
        check(modificationItems.contains(item))
        modificationItems.remove(item)
    }
}

class IntStatValue(statType: StatType) : StatValue<Int>(statType, 0, 0) {
    override fun recalculateBase(experience: Experience) {
        val newBase = statType.calculateBaseValueFor(experience)
        check(newBase is Int)
        base = newBase
    }

    override fun recalculateCurrent() {
        var tempCurrent = base
        modificationItems.forEach {
            tempCurrent = it.modificationType.calculateNewCurrent(base, tempCurrent, it.value)
        }
        current = tempCurrent
    }
}

class FloatStatValue(statType: StatType) : StatValue<Float>(statType, 0f, 0f) {
    override fun recalculateBase(experience: Experience) {
        val newBase = statType.calculateBaseValueFor(experience)
        check(newBase is Float)
        base = newBase
    }

    override fun recalculateCurrent() {
        var tempCurrent = base
        modificationItems.forEach {
            tempCurrent = it.modificationType.calculateNewCurrent(base, tempCurrent, it.value)
        }
        current = tempCurrent
    }
}
