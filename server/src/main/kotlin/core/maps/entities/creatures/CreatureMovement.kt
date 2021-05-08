package core.maps.entities.creatures

import core.types.WorldPosition

class CreatureMovement(
    private val creature: Creature
) {
    private var checkpoints: List<WorldPosition>? = null
        private set

    fun onInit() {
        //nothing
    }

    fun isMoving(): Boolean {
        return checkpoints != null
    }

    fun currentCheckpoint(): WorldPosition {
        return checkpoints?.last()!!
    }

    fun removeCurrentCheckpoint() {
        checkpoints = checkpoints?.dropLast(1)
        if (checkpoints.isNullOrEmpty()) {
            stopMoving()
        }
    }

    fun stopMoving() {
        checkpoints = null
//        creature.physics.fixture.body.setLinearVelocity(0f, 0f)

    }

    fun startMovingTo(targetPosition: WorldPosition) {
        checkpoints = creature.gameMap.navigation.findPath(creature.position, targetPosition).reversed().dropLast(1)
    }
}
