package core.maps.entities.creatures

class CreatureStats(private val creature: Creature) {
    val defense = IntStatValue(StatType.DEFENSE)
    val attack = IntStatValue(StatType.ATTACK)
    val attackSpeed = FloatStatValue(StatType.ATTACK_SPEED)
    val movementSpeed = FloatStatValue(StatType.MOVEMENT_SPEED)
    val healthPool = IntStatValue(StatType.HEALTH_POOL)

    var healthCurrent: Int = 0

    init {
        updateBase()
        updateCurrent()
        healthCurrent = healthPool.current
    }

    fun updateBase() {
        defense.updateBase(creature.experience)
        attack.updateBase(creature.experience)
        attackSpeed.updateBase(creature.experience)
        movementSpeed.updateBase(creature.experience)
        healthPool.updateBase(creature.experience)
    }

    fun updateCurrent() {
        defense.updateCurrent()
        attack.updateCurrent()
        attackSpeed.updateCurrent()
        movementSpeed.updateCurrent()
        healthPool.updateCurrent()
    }
}

