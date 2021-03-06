package core.maps


import core.StateChangeNotifier
import core.maps.entities.Creature
import core.maps.entities.GameMap
import core.maps.entities.Item
import core.maps.entities.Player
import core.types.*
import mu.KotlinLogging
import org.mini2Dx.gdx.math.Vector2

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
        map.creatures.forEach { creatures[it.cid] = it }
    }

    fun addPlayer(player: Player) {
        players[player.pid] = player
        creatures[player.cid] = player

        notifier.notifyPlayerDetails(player.pid, player)
        // Notify me about me
        notifier.notifyCreatureUpdate(player.pid, player)

        map.getTileAt(GameMap.GridPosition(Coordinate(0), Coordinate(0))).putCreature(player)
        movePlayerTo(player, Vector2(400f, 400f))

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

    fun movePlayerBy(pid: PID, vector: Vector2) {
        val player = getPlayer(pid)

        val newPosition = player.position.cpy().mulAdd(vector, player.movementSpeed)
        movePlayerTo(player, newPosition)
    }

    fun movePlayerTo(player: Player, position: WorldPosition) {
        val newPosition = position
        val newGridCoords = GameMap.toGridPosition(position)

        val oldPosition = player.position
        val olcGridCoords = GameMap.toGridPosition(oldPosition)

        player.position = newPosition

        val tileChanged = olcGridCoords != newGridCoords
        /*
        [1,2,3,4] -> [3,4,5,6]

        [1, 2] -> Disappear
        [3, 4] -> Creature Position Update
        [5, 6] -> Creature Update
         */
        if (tileChanged) {
            val oldVisibleCreatures = player.getVisibleCreatures()
            player.lastUpdate.gridPosition = newGridCoords
            player.lastUpdate.tileSlice = map.getTilesAround(newGridCoords, player.viewRadius.toInt())
            val newVisibleCreatures = player.getVisibleCreatures()

            val oldTile = map.getTileAt(olcGridCoords)
            val newTile = map.getTileAt(newGridCoords)
            oldTile.moveCreatureToTile(player, newTile)

            // Disappear
            (oldVisibleCreatures subtract newVisibleCreatures).forEach { otherCreature ->
                if (otherCreature is Player) {
                    notifier.notifyCreatureDisappear(otherCreature.pid, player)
                }
                notifier.notifyCreatureDisappear(player.pid, otherCreature)
            }

            // Creature Position Update
            (oldVisibleCreatures intersect newVisibleCreatures).forEach { otherCreature ->
                if (otherCreature is Player) {
                    notifier.notifyCreaturePositionUpdate(otherCreature.pid, player)
                }
            }

            // Creature Update
            (newVisibleCreatures subtract oldVisibleCreatures).forEach { otherCreature ->
                if (otherCreature is Player) {
                    notifier.notifyCreatureUpdate(otherCreature.pid, player)
                }
                notifier.notifyCreatureUpdate(player.pid, otherCreature)
            }

            // Notify me about terrain change
            notifier.notifyTerrainUpdate(player)
            notifier.notifyTerrainItemsUpdate(player)
        } else {
            // Notify others about me
            getVisiblePlayersOf(player) { otherPlayer ->
                println(otherPlayer)
                notifier.notifyCreaturePositionUpdate(otherPlayer.pid, player)
            }
        }

        // Notify me about me
        notifier.notifyCreaturePositionUpdate(player.pid, player)

        checkItemsCollisions(player)
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

    private fun checkItemsCollisions(player: Player) {
        player.getVisibleItems().forEach { item ->
            if (item.collidesWith(player.position)) {
                item.actionHandler.onItemWalkedOn(this, player, item)
            }
        }
    }

    private fun getPlayer(pid: PID): Player {
        return players[pid] ?: throw Error("PlayerController not found for ${pid}")
    }

    private fun getItem(iid: IID): Item {
        return items[iid] ?: throw Error("GameMap.Item not found for ${iid}")
    }

    private fun getVisiblePlayersOf(player: Player, callback: (otherPlayer: Player) -> Unit) {
        player.getVisiblePlayers().forEach(callback)
    }

    private fun getVisibleCreaturesOf(player: Player, callback: (otherPlayer: Creature) -> Unit) {
        player.getVisibleCreatures().forEach(callback)
    }

    private fun notifyEveryone(callback: (pid: PID) -> Unit) {
        players.keys.forEach { pid -> callback.invoke(pid) }
    }
}
