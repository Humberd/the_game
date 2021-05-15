package core.maps.entities

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import core.maps.entities.creatures.player.Player
import core.maps.entities.creatures.player.PlayerSeed
import core.maps.entities.projectiles.Projectile
import core.maps.entities.projectiles.ProjectileSeed
import core.types.GridPosition
import ktx.box2d.BodyDefinition
import pl.humberd.models.PID

interface GameContext {
    fun create(type: BodyDef.BodyType = BodyDef.BodyType.StaticBody, init: BodyDefinition.() -> Unit = {}): Body
    fun create(seed: PlayerSeed): Player
    fun create(seed: ProjectileSeed): Projectile
    fun destroy(body: Body)
    fun destroy(pid: PID)
    fun getTilesAround(position: GridPosition, radius: Int): Array<Array<Tile>>
    fun findPath(start: Vector2, end: Vector2): List<Vector2>
}
