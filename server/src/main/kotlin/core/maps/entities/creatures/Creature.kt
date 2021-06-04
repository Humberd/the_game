package core.maps.entities.creatures

import core.maps.entities.CollisionCategory
import core.maps.entities.Entity
import core.maps.entities.GameContext
import core.types.CreatureName
import core.types.TileRadius
import core.types.WorldPosition
import mu.KLogging
import pl.humberd.models.CID
import pl.humberd.models.Experience

abstract class Creature(
    private val creatureSeed: CreatureSeed,
    val context: GameContext,
) : Entity {
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
    val spells = CreatureSpells(this)


    override fun onInit() {
        lastUpdate.onInit(creatureSeed.position)
        physics.onInit(creatureSeed.position)
        fov.onInit()
        stats.onInit()
        movement.onInit()
        equipment.onInit(creatureSeed.equipment)
        backpack.onInit(creatureSeed.backpack)
        spells.onInit()
    }

    override fun onUpdate(deltaTime: Float) {
        movement.onUpdate(deltaTime)
        fov.onUpdate(deltaTime)
        spells.onUpdate(deltaTime)
    }

    override fun onDestroy() {
        physics.onDestroy()
        fov.onDestroy()
        spells.onDestroy()
    }

    abstract val hooks: CreatureHooks
    abstract val collisionCategory: CollisionCategory

    fun canSee(otherCreature: Creature): Boolean {
        return fov.creatures.canISeeThem(otherCreature)
    }
}
