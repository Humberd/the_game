package core.maps.entities.creatures

import core.StateChangeNotifier
import core.maps.entities.CollisionCategory
import core.maps.entities.GameMap
import core.types.CreatureName
import core.types.TileRadius
import core.types.WorldPosition
import ktx.math.minus
import ktx.math.plus
import ktx.math.times
import mu.KLogging
import pl.humberd.models.CID
import pl.humberd.models.Experience
import utils.angleRadTo
import utils.getDistance
import utils.toGridPosition

abstract class Creature(
    private val creatureSeed: CreatureSeed,
    val gameMap: GameMap,
    val notifier: StateChangeNotifier
) {

    companion object : KLogging()

    //region Properties
    val cid = CID.unique()

    var name: CreatureName = creatureSeed.name
        private set

    var experience: Experience = creatureSeed.experience
        private set

    val position: WorldPosition
        get() = physics.body.position

    val rotation: Float
        get() = physics.body.angle

    var tilesViewRadius: TileRadius = creatureSeed.tilesViewRadius
        private set

    val bodyRadius: Float = creatureSeed.bodyRadius
    //endregion

    val lastUpdate = CreatureLastUpdate(this)
    val physics = CreaturePhysics(this)
    val fov = CreatureFov(this)
    val stats = CreatureStats(this)
    val movement = CreatureMovement(this)
    val combat = CreatureCombat(this)
    val equipment = CreatureEquipment(this)
    val backpack = CreatureBackpack(this)


    open fun onInit() {
        lastUpdate.onInit(creatureSeed.position)
        physics.onInit(creatureSeed.position)
        fov.onInit()
        stats.onInit()
        movement.onInit()
        equipment.onInit(creatureSeed.equipment)
        backpack.onInit(creatureSeed.backpack)
    }

    open fun onDestroy() {
        physics.onDestroy()
        fov.onDestroy()
    }

    abstract val hooks: CreatureHooks
    abstract val collisionCategory: CollisionCategory

    open fun afterPhysicsUpdate(deltaTime: Float) {
        if (!movement.isMoving()) {
            return
        }

        val nextCheckpoint = movement.currentCheckpoint()
        val positionToCheckpointDistance = getDistance(position, nextCheckpoint)

        val velocity = (nextCheckpoint - position).nor() * deltaTime * stats.movementSpeed.current
        val nextPosition = position + velocity
        val positionToNextPositionDistance = getDistance(position, nextPosition)
        val targetPosition = if (positionToCheckpointDistance <= positionToNextPositionDistance) {
            movement.removeCurrentCheckpoint()
            nextCheckpoint
        } else {
            nextPosition
        }
        physics.body.setTransform(targetPosition, position.angleRadTo(targetPosition))


        // update tiles in grid
        val oldGridCoords = lastUpdate.gridPosition
        val newGridCoords = toGridPosition(position)
        val tileChanged = oldGridCoords != newGridCoords
        if (tileChanged) {
            lastUpdate.gridPosition = newGridCoords
            lastUpdate.tileSlice = gameMap.getTilesAround(newGridCoords, tilesViewRadius.value)
        }

        hooks.onMoved(tileChanged)
        fov.creatures.theySeeMe().forEach {
            it.hooks.onOtherCreaturePositionChange(this)
        }
    }

    fun canSee(otherCreature: Creature): Boolean {
        return fov.creatures.canISeeThem(otherCreature)
    }
}
