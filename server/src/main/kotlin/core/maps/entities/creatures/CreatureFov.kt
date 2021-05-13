package core.maps.entities.creatures

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import core.maps.entities.Collider
import core.maps.entities.CollisionCategory
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.filter
import mu.KLogging
import pl.humberd.misc.exhaustive

class CreatureFov(private val thisCreature: Creature) : Collider.WithAnything {
    companion object : KLogging()

    private lateinit var tileViewSensor: Body

    fun onInit() {
        tileViewSensor = thisCreature.gameMap.physics.body(BodyDef.BodyType.DynamicBody) {
            box(
                width = thisCreature.tilesViewRadius.value.toFloat() * 2,
                height = thisCreature.tilesViewRadius.value.toFloat() * 2
            ) {
                density = 0f
                friction = 0f
                restitution = 0f
                filter {
                    categoryBits = CollisionCategory.DETECTION.value
                    maskBits = CollisionCategory.DETECTION.collidesWith()
                }
                isSensor = true
                userData = this@CreatureFov
            }

        }.also { thisCreature.physics.joinWithMainBody(it) }
    }

    fun onDestroy() {
        thisCreature.gameMap.physics.destroyBody(tileViewSensor)
    }

    val projectiles = ProjectileVisibilityStore()

    inner class ProjectileVisibilityStore

    val creatures = CreatureVisibilityStore()

    inner class CreatureVisibilityStore : Collider.WithCreature {
        private val iSeeThem = HashSet<Creature>()
        private val theySeeMe = HashSet<Creature>()

        override fun onCollisionStart(entity: Creature) {
            if (entity === thisCreature) {
                return
            }
            logger.info { "Collision start with $entity" }

            check(!iSeeThem.contains(entity)) { "$thisCreature already sees $entity" }
            iSeeThem.add(entity)
            check(!entity.fov.creatures.theySeeMe.contains(thisCreature)) { "$entity is already seen by $thisCreature" }
            entity.fov.creatures.theySeeMe.add(thisCreature)

            thisCreature.hooks.onOtherCreatureAppearInViewRange(entity)
        }

        override fun onCollisionEnd(entity: Creature) {
            if (entity === thisCreature) {
                return
            }
            logger.info { "Collision end with $entity" }

            check(iSeeThem.contains(entity)) { "$thisCreature doesn't already see $entity" }
            iSeeThem.remove(entity)
            check(entity.fov.creatures.theySeeMe.contains(thisCreature)) { "$entity is not already seen by $thisCreature" }
            entity.fov.creatures.theySeeMe.remove(thisCreature)

            thisCreature.hooks.onOtherCreatureDisappearFromViewRange(entity)
        }

        fun canISeeThem(entity: Creature) = iSeeThem.contains(entity)
        fun canTheySeeMe(entity: Creature) = theySeeMe.contains(entity)
        fun iSeeThem(): Set<Creature> = iSeeThem
        fun theySeeMe(): Set<Creature> = theySeeMe
    }

    override fun onCollisionStart(entity: Any) {
        when (entity) {
            is Creature -> creatures.onCollisionStart(entity)
            else -> logger.info { "Collision start $this with $entity not supported" }
        }.exhaustive
    }

    override fun onCollisionEnd(entity: Any) {
        when (entity) {
            is Creature -> creatures.onCollisionEnd(entity)
            else -> logger.info { "Collision end $this with $entity not supported" }
        }.exhaustive
    }

    override fun toString() = "CreatureFov(${thisCreature.cid})"
}
