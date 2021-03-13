package core.maps


import core.StateChangeNotifier
import core.maps.entities.*
import core.types.*
import infrastructure.udp.egress.EgressDataPacket
import mu.KotlinLogging
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
        player.createPhysicsBody(map.physicsWorld)

        notifier.notifyPlayerDetails(player.pid, player)
        // Notify me about me
        notifier.notifyCreatureUpdate(player.pid, player)
    }

    fun removePlayer(pid: PID) {
        val player = getPlayer(pid)

        players.remove(pid)

        // Notify me about me
        notifier.notifyCreatureDisappear(player.pid, player)
    }

    fun moveToV2(pid: PID, position: WorldPosition) {
        getPlayer(pid).startMovingTo(position)
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

    fun onPhysicsStep(deltaTime: Float) {
        map.onPhysicsStep(deltaTime)

        players.values.forEach { creature ->
            creature.afterPhysicsUpdate(deltaTime)
        }
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
