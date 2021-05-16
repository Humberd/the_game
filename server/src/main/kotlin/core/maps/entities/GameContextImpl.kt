package core.maps.entities

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import core.PlayerNotifier
import core.maps.entities.creatures.monster.Monster
import core.maps.entities.creatures.player.Player
import core.maps.entities.creatures.player.PlayerSeed
import core.maps.entities.projectiles.Projectile
import core.maps.entities.projectiles.ProjectileSeed
import core.types.GameId
import core.types.GridPosition
import ktx.box2d.BodyDefinition
import ktx.box2d.body
import ktx.box2d.createWorld
import mu.KLogging
import pl.humberd.models.PID

class GameContextImpl(
    private val playerNotifier: PlayerNotifier,
    gameMapSeed: GameMapSeed,
    val gameId: GameId
) : GameContext, Lifecycle {
    companion object : KLogging()

    val gameMap = GameMap(gameMapSeed, this)
    val physics = createWorld(gravity = Vector2(0f, 0f), allowSleep = false)

    val projectiles = HashSet<Projectile>()
    val creatures = GameMapCreaturesContainer()

    init {
        gameMapSeed.monsterSeeds.forEach {
            creatures.add(
                Monster(
                    monsterSeed = it,
                    context = this
                )
            )
        }
    }

    override fun onInit() {
        gameMap.onInit()
        physics.setContactListener(GameMapContactListener())
        logger.info { "Hello" }
        GameMapDebugRenderer(gameMap.navigation, physics)
    }

    override fun onUpdate(deltaTime: Float) {
        physics.step(deltaTime, 6, 2)
        projectiles.forEach { it.onUpdate(deltaTime) }
        creatures.getAllCreatures().forEach { it.onUpdate(deltaTime) }
    }

    override fun onDestroy() {
        projectiles.forEach { it.onDestroy() }
        creatures.getAllCreatures().forEach { it.onDestroy() }
    }

    override fun create(type: BodyDef.BodyType, init: BodyDefinition.() -> Unit): Body {
        return physics.body(type, init)
    }

    override fun create(seed: PlayerSeed): Player {
        return Player(
            seed,
            this,
            playerNotifier
        ).also { creatures.add(it) }
    }

    override fun create(seed: ProjectileSeed): Projectile {
        return Projectile(
            seed,
            this
        ).also {
            projectiles.add(it)
            it.onInit()
        }
    }

    override fun destroy(body: Body) {
        physics.destroyBody(body)
    }

    override fun destroy(pid: PID) {
        creatures.remove(pid)
    }

    override fun destroy(entity: Projectile) {
        require(projectiles.contains(entity))
        projectiles.remove(entity)
        entity.onDestroy()
    }

    override fun getTilesAround(position: GridPosition, radius: Int): Array<Array<Tile>> {
        return gameMap.getTilesAround(position, radius)
    }

    override fun findPath(start: Vector2, end: Vector2): List<Vector2> {
        return gameMap.navigation.findPath(start, end)
    }
}
