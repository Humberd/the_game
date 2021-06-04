package core.maps.entities

import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.player.Player
import core.types.WorldPosition
import mu.KLogging
import pl.humberd.models.CID
import pl.humberd.models.PID

class GameMapCreaturesContainer {
    companion object : KLogging()

    private val players = HashMap<PID, Player>()
    private val creatures = HashMap<CID, Creature>()

    fun add(creature: Creature) {
        if (creatures.containsKey(creature.cid)) {
            throw Error("Creature already exists")
        }

        if (creature is Player) {
            if (players.containsKey(creature.pid)) {
                // fixme: do nothing
//                throw Error("Player already exists")
            }
            players[creature.pid] = creature
        }

        creatures[creature.cid] = creature

        creature.onInit()
        creature.hooks.onAddedToMap()
    }

    fun remove(pid: PID) {
        if (!players.containsKey(pid)) {
            throw Error("Player doesn't exist")
        }

        remove(players[pid]!!.cid)
        players.remove(pid)
    }

    fun remove(cid: CID) {
        if (!creatures.containsKey(cid)) {
            throw Error("Creature doesn't exist")
        }
        val removedCreature = creatures.remove(cid)
        removedCreature!!.hooks.onRemovedFromMap()
        removedCreature.onDestroy()
        logger.info { "Creature removed $removedCreature" }
    }

    fun get(pid: PID): Player {
        return players[pid] ?: throw Error("Player not found")
    }

    fun get(cid: CID): Creature {
        return creatures[cid] ?: throw Error("Creature not found")
    }

    fun getAllPlayers(): Collection<Player> {
        return players.values
    }

    fun getAllCreatures(): Collection<Creature> {
        return creatures.values
    }

    fun moveTo(pid: PID, targetPosition: WorldPosition) {
        moveTo(get(pid).cid, targetPosition)
    }

    fun moveTo(cid: CID, targetPosition: WorldPosition) {
        val creature = get(cid)
        creature.movement.startMovingTo(targetPosition)
    }
}
