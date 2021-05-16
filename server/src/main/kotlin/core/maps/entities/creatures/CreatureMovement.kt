package core.maps.entities.creatures

import core.maps.entities.Lifecycle
import core.types.WorldPosition
import ktx.math.minus
import ktx.math.plus
import ktx.math.times
import utils.angleRadTo
import utils.getDistance
import utils.toGridPosition

class CreatureMovement(
    private val creature: Creature
) : Lifecycle {
    private var checkpoints: List<WorldPosition>? = null
        private set

    override fun onUpdate(deltaTime: Float) {
        if (!isMoving()) {
            return
        }

        val nextCheckpoint = currentCheckpoint()
        val positionToCheckpointDistance = getDistance(creature.position, nextCheckpoint)

        val velocity = (nextCheckpoint - creature.position).nor() * deltaTime * creature.stats.movementSpeed.current
        val nextPosition = creature.position + velocity
        val positionToNextPositionDistance = getDistance(creature.position, nextPosition)
        val targetPosition = if (positionToCheckpointDistance <= positionToNextPositionDistance) {
            removeCurrentCheckpoint()
            nextCheckpoint
        } else {
            nextPosition
        }
        creature.physics.body.setTransform(targetPosition, creature.position.angleRadTo(targetPosition))


        // update tiles in grid
        val oldGridCoords = creature.lastUpdate.gridPosition
        val newGridCoords = toGridPosition(creature.position)
        val tileChanged = oldGridCoords != newGridCoords
        if (tileChanged) {
            creature.lastUpdate.gridPosition = newGridCoords
            creature.lastUpdate.tileSlice =
                creature.context.getTilesAround(newGridCoords, creature.tilesViewRadius.value)
        }

        creature.hooks.onMoved(tileChanged)
        creature.fov.creatures.theySeeMe().forEach {
            it.hooks.onOtherCreaturePositionChange(creature)
        }
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
    }

    fun startMovingTo(targetPosition: WorldPosition) {
        checkpoints = creature.context.findPath(creature.position, targetPosition).reversed().let {
            if (it.size > 1) {
                it.dropLast(1)
            } else {
                null
            }
        }
    }
}
