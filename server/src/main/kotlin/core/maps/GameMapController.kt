package core.maps


import core.StateChangeNotifier
import core.maps.entities.*
import core.types.*
import infrastructure.udp.egress.EgressDataPacket
import mu.KotlinLogging
import org.mini2Dx.gdx.math.Vector2
import utils.ms

private val logger = KotlinLogging.logger {}

class GameMapController(
    private val notifier: StateChangeNotifier,
    private val map: GameMap
) {
    private val players = HashMap<PID, Player>()
    private val items = HashMap<IID, Item>()
    private val creatures = HashMap<CID, Creature>()

    init {
        map.items.forEach { items[it.iid] = it }
        map.creatures.forEach {
            creatures[it.cid] = it
            it.connectWithMap(this, notifier)
        }
    }

    fun addPlayer(player: Player) {
        players[player.pid] = player
        creatures[player.cid] = player
        player.connectWithMap(this, notifier)

        notifier.notifyEquippedSpellsChange(player)
        notifier.notifyPlayerDetails(player.pid, player)
        // Notify me about me
        notifier.notifyCreatureUpdate(player.pid, player)

        map.getTileAt(GameMap.GridPosition(Coordinate(0), Coordinate(0))).putCreature(player)
        moveTo(player, Vector2(400f, 400f))

        // When first connecting get me a full list of other creatures
        getVisibleCreaturesOf(player) { otherPlayer ->
            notifier.notifyCreatureUpdate(player.pid, otherPlayer)
        }
    }

    fun removePlayer(pid: PID) {
        val player = getPlayer(pid)

        players.remove(pid)
        creatures.remove(player.cid)

        val tile = map.getTileFor(player)
        tile.removeCreature(player.cid)

        // Notify others about my disappearance
        player.getVisiblePlayers().forEach { otherPlayer ->
            notifier.notifyCreatureDisappear(otherPlayer.pid, player)
        }
        // Notify me about me
        notifier.notifyCreatureDisappear(player.pid, player)
    }

    fun moveBy(pid: PID, vector: Vector2) {
        moveBy(getPlayer(pid), vector)
    }

    fun moveBy(cid: CID, vector: Vector2) {
        moveBy(getCreature(cid), vector)
    }

    fun moveBy(creature: Creature, vector: Vector2) {
        val newPosition = creature.position.cpy().mulAdd(vector, creature.movementSpeed)
        moveTo(creature, newPosition)
    }

    fun moveTo(creature: Creature, position: WorldPosition) {
        val newPosition = position
        val newGridCoords = GameMap.toGridPosition(position)

        val oldPosition = creature.position
        val oldGridCoords = GameMap.toGridPosition(oldPosition)

        creature.position = newPosition

        val tileChanged = oldGridCoords != newGridCoords
        /*
        [1,2,3,4] -> [3,4,5,6]

        [1, 2] -> Disappear
        [3, 4] -> Creature Position Update
        [5, 6] -> Creature Update
         */
        if (tileChanged) {
            val oldVisibleCreatures = creature.getVisibleCreatures()
            creature.lastUpdate.gridPosition = newGridCoords
            creature.lastUpdate.tileSlice = map.getTilesAround(newGridCoords, creature.tilesViewRadius.value)
            val newVisibleCreatures = creature.getVisibleCreatures()

            val oldTile = map.getTileAt(oldGridCoords)
            val newTile = map.getTileAt(newGridCoords)
            oldTile.moveCreatureToTile(creature, newTile)

            // Creature Disappear
            (oldVisibleCreatures subtract newVisibleCreatures).forEach { otherCreature ->
                // fixme: we are assuming they have the same view range
                otherCreature.onOtherCreatureDisappearFromViewRange(creature)
                creature.onOtherCreatureDisappearFromViewRange(otherCreature)
            }

            // Creature Position Update
            (oldVisibleCreatures intersect newVisibleCreatures).forEach { otherCreature ->
                // fixme: we are assuming they have the same view range
                otherCreature.onOtherCreaturePositionChange(creature)
            }

            // Creature Appear
            (newVisibleCreatures subtract oldVisibleCreatures).forEach { otherCreature ->
                // fixme: we are assuming they have the same view range
                otherCreature.onOtherCreatureAppearInViewRange(creature)
                creature.onOtherCreatureAppearInViewRange(otherCreature)
            }

            if (creature is Player) {
                // Notify me about terrain change
                notifier.notifyTerrainUpdate(creature)
                notifier.notifyTerrainItemsUpdate(creature)
            }

        } else {
            // Notify others about me
            getVisiblePlayersOf(creature) { otherPlayer ->
                otherPlayer.onOtherCreaturePositionChange(creature)
            }
        }

        if (creature is Player) {
            // Notify me about me
            notifier.notifyCreaturePositionUpdate(creature.pid, creature)
        }

        checkItemsCollisions(creature)
    }

    fun moveItemOnTerrain(pid: PID, iid: IID, targetPosition: WorldPosition) {
        val item = getItem(iid)
        if (!item.itemDef.isMovable) {
            logger.debug { "Cannot move immovable object" }
            return
        }

        val currentItemTile = map.getTileFor(item)

        val targetItemCoords = GameMap.toGridPosition(targetPosition)
        val targetItemTile = map.getTileAt(targetItemCoords)

        currentItemTile.moveItemToTile(item, targetItemTile)
        item.position = targetPosition

        notifyEveryone { otherPID -> notifier.notifyTerrainItemsUpdate(getPlayer(otherPID)) }
    }

    fun useSpell(pid: PID, sid: SID) {
        val player = getPlayer(pid)
        val spell = player.spellsContainer.getSpell(sid)
        executeSpell(player, spell)
    }

    private fun executeSpell(player: Player, spell: Spell) {
        val spellUse = EgressDataPacket.SpellUse(
            sourcePosition = player.position,
            effects = arrayOf(
                EgressDataPacket.SpellUse.SpellEffect(SpriteId(7u), 500.ms),
                EgressDataPacket.SpellUse.SpellEffect(SpriteId(7u), 500.ms),
                EgressDataPacket.SpellUse.SpellEffect(SpriteId(7u), 500.ms),
                EgressDataPacket.SpellUse.SpellEffect(SpriteId(7u), 500.ms)
            )
        )
        notifier.notifySpellUse(player.pid, spellUse)
    }

    private fun checkItemsCollisions(creature: Creature) {
        creature.getVisibleItems().forEach { item ->
            if (item.collidesWith(creature.position)) {
                item.actionHandler.onItem__(12)
                item.actionHandler.onItemWalkedOn(creature.scriptable, item)
            }
        }
    }

    private fun getCreature(cid: CID): Creature {
        return creatures[cid] ?: throw Error("Creature not found for")
    }

    private fun getPlayer(pid: PID): Player {
        return players[pid] ?: throw Error("Player not found for ${pid}")
    }

    private fun getItem(iid: IID): Item {
        return items[iid] ?: throw Error("GameMap.Item not found for ${iid}")
    }

    private fun getVisiblePlayersOf(creature: Creature, callback: (otherPlayer: Player) -> Unit) {
        creature.getVisiblePlayers().forEach(callback)
    }

    private fun getVisibleCreaturesOf(creature: Creature, callback: (otherPlayer: Creature) -> Unit) {
        creature.getVisibleCreatures().forEach(callback)
    }

    private fun notifyEveryone(callback: (pid: PID) -> Unit) {
        players.keys.forEach { pid -> callback.invoke(pid) }
    }
}
