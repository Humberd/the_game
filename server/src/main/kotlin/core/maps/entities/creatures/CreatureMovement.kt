package core.maps.entities.creatures

import core.types.WorldPosition

class CreatureMovement(
    private val creature: Creature
) {
    var targetPosition: WorldPosition? = null
        private set

    fun onInit() {
        //nothing
    }

    fun isMoving(): Boolean {
        return targetPosition != null
    }

    fun stopMoving() {
        targetPosition = null
        creature.physics.fixture.body.setLinearVelocity(0f, 0f)
    }

    fun startMovingTo(targetPosition: WorldPosition) {
        val velocity = targetPosition.cpy().sub(creature.position).nor()
        velocity.x *= creature.stats.movementSpeed.current
        velocity.y *= creature.stats.movementSpeed.current
        this.targetPosition = targetPosition
        creature.physics.fixture.body.setLinearVelocity(velocity)
    }
}
