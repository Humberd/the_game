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
    val cache = CreatureCache(this)
    val stats = CreatureStats(this)
    val movement = CreatureMovement(this)
    val combat = CreatureCombat(this)
    val equipment = CreatureEquipment(this)
    val backpack = CreatureBackpack(this)


    open fun onInit() {
        lastUpdate.onInit(creatureSeed.position)
        physics.onInit(creatureSeed.position)
        fov.onInit()
        cache.onInit()
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
            gameMap.getTileAt(oldGridCoords).creatures.transferTo(cid, this, gameMap.getTileAt(newGridCoords).creatures)

            /*
            [1,2,3,4] -> [3,4,5,6]

            [1, 2] -> Creature Disappear
            [3, 4] -> Creature Position Update
            [5, 6] -> Creature Appear
             */

            // Creature Disappear
            ArrayList(cache.creaturesThatSeeMe).forEach {
                if (!it.canSee(this)) {
                    it.cache.creaturesISee.unregister(this)
                }
            }
            cache.creaturesISee.getAll().forEach {
                if (!canSee(it)) {
                    cache.creaturesISee.unregister(it)
                }
            }

            // Creature Appear
            gameMap.creatures.getAllCreatures()
                .filter { it.cid != cid }
                .filter { it.canSee(this) }
                .subtract(cache.creaturesThatSeeMe)
                .forEach {
                    it.cache.creaturesISee.register(this)
                }
            getGreedyVisibleCreatures()
                .subtract(cache.creaturesISee.getAll())
                .forEach {
                    cache.creaturesISee.register(it)
                }

        }

        hooks.onMoved(tileChanged)
        cache.creaturesThatSeeMe.forEach {
            it.hooks.onOtherCreaturePositionChange(this)
        }
    }

    fun getGreedyVisibleCreatures(): List<Creature> {
        val buffer = arrayListOf<Creature>()

        lastUpdate.tileSlice.forEach {
            it.forEach {
                it.creatures.writeTo(buffer)
            }
        }

        return buffer.filter { it.cid != cid }
    }

    fun canSee(otherCreature: Creature): Boolean {
        return getGreedyVisibleCreatures().contains(otherCreature)
    }
}
