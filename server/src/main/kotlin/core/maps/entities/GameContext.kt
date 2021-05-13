package core.maps.entities

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import core.maps.entities.creatures.player.PlayerSeed
import core.maps.entities.projectiles.Projectile
import core.maps.entities.projectiles.ProjectileSeed
import ktx.box2d.BodyDefinition
import ktx.box2d.body

abstract class GameContext {
    abstract val physics: World
    val projectiles = HashSet<Projectile>()
    val creatures = GameMapCreaturesContainer()


    fun create(type: BodyDef.BodyType = BodyDef.BodyType.StaticBody, init: BodyDefinition.() -> Unit = {}): Body {
        return physics.body(type, init)
    }

    fun create(seed: PlayerSeed) {
    }

    fun create(seed: ProjectileSeed): Projectile {
        return Projectile(
            seed,
            this
        ).also { projectiles.add(it) }
    }

    fun destroy(body: Body) {
        physics.destroyBody(body)
    }
}
