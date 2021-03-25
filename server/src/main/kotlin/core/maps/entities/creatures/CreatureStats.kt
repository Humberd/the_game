package core.maps.entities.creatures

class CreatureStats(private val creature: Creature) {
    val defense = IntStatValue(StatType.DEFENSE)
    val attack = IntStatValue(StatType.ATTACK)
    val attackSpeed = FloatStatValue(StatType.ATTACK_SPEED)
    val movementSpeed = FloatStatValue(StatType.MOVEMENT_SPEED)
    val healthPool = IntStatValue(StatType.HEALTH_POOL)

    var healthCurrent: Int = 0

    fun onInit() {
        updateBase()
        recalculateCurrent()
        healthCurrent = healthPool.current
    }

    fun updateBase() {
        defense.recalculateBase(creature.experience)
        attack.recalculateBase(creature.experience)
        attackSpeed.recalculateBase(creature.experience)
        movementSpeed.recalculateBase(creature.experience)
        healthPool.recalculateBase(creature.experience)
    }

    fun recalculateCurrent() {
        defense.recalculateCurrent()
        attack.recalculateCurrent()
        attackSpeed.recalculateCurrent()
        movementSpeed.recalculateCurrent()
        healthPool.recalculateCurrent()
    }
}

