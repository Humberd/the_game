package core

import com.badlogic.gdx.physics.box2d.ChainShape
import com.badlogic.gdx.physics.box2d.Shape
import core.maps.entities.GameMap
import core.maps.entities.creatures.Creature
import core.maps.entities.creatures.monster.Monster
import core.maps.entities.creatures.player.Player
import core.types.WorldPosition
import infrastructure.udp.ServerUdpSendQueue
import infrastructure.udp.egress.UdpEgressPacketHandler
import infrastructure.udp.models.convert
import pl.humberd.models.ApiVector2
import pl.humberd.models.PID
import pl.humberd.udp.packets.serverclient.*

class StateChangeNotifier(
    private val egressPacketHandler: UdpEgressPacketHandler,
    private val queue: ServerUdpSendQueue
) {
    fun notifyCreatureUpdate(to: Player, creature: Creature) {
        queue.put(
            to.pid,
            CreatureUpdate(
                cid = creature.cid,
                name = creature.name.value,
                baseHealth = creature.stats.healthPool.current,
                currentHealth = creature.stats.healthCurrent,
                position = creature.position.convert(),
                spriteId = creature.spriteId.value,
                bodyRadius = creature.bodyRadius,
                attackTriggerRadius = if (creature is Monster) creature.attackTriggerRadius else 0f,
                isBeingAttackedByMe = to.combat.attackedTarget === creature
            )
        )
    }

    fun notifyCreatureDisappear(to: PID, creature: Creature) {
        queue.put(
            to,
            CreatureDisappear(creature.cid)
        )
    }

    fun notifyCreaturePositionUpdate(to: PID, creature: Creature) {
        queue.put(
            to,
            CreaturePositionUpdate(
                cid = creature.cid,
                position = creature.position.convert()
            )
        )
    }

    fun notifyPlayerDetails(to: PID, player: Player) {
        queue.put(
            to,
            PlayerDetails(
                pid = player.pid,
                cid = player.cid
            )
        )
    }

    fun notifyTerrainUpdate(player: Player) {
        val tiles = player.lastUpdate.tileSlice

        queue.put(
            player.pid,
            TerrainUpdate(
                windowWidth = tiles.size.toUByte(),
                windowHeight = if (tiles.size > 0) tiles[0].size.toUByte() else 0u,
                windowGridStartPositionX = tiles.let {
                    if (it.size == 0) {
                        return@let 0
                    }
                    if (it[0].size == 0) {
                        return@let 0
                    }
                    it[0][0].gridPosition.x.value.toShort()
                },
                windowGridStartPositionY = tiles.let {
                    if (it.size == 0) {
                        return@let 0
                    }
                    if (it[0].size == 0) {
                        return@let 0
                    }
                    it[0][0].gridPosition.y.value.toShort()
                },
                spriteIds = tiles.flatten().map { it.spriteId.value }.toTypedArray()
            )
        )
    }


    fun notifyEquippedSpellsChange(player: Player) {
        queue.put(
            player.pid,
            EquippedSpellsUpdate(
                spells = listOf(
                    player.spellsContainer.spell1,
                    player.spellsContainer.spell2,
                    player.spellsContainer.spell3,
                    player.spellsContainer.spell4
                ).map {
                    return@map if (it == null) {
                        null
                    } else {
                        EquippedSpellsUpdate.SpellUpdate(
                            sid = it.sid,
                            name = it.name,
                            spriteId = it.spriteId.value,
                            cooldown = it.cooldown
                        )
                    }
                }.toTypedArray()
            )
        )
    }

    fun notifySpellUse(to: PID, spellUse: SpellUse) {
        queue.put(
            to,
            spellUse
        )
    }

    fun notifyDamageTaken(to: PID, damages: Array<DamageTaken.Damage>) {
        queue.put(
            to,
            DamageTaken(damages)
        )
    }

    fun sendProjectile(to: PID, data: ProjectileSend) {
        queue.put(to, data)
    }

    fun notifyEquipmentUpdate(player: Player) {
//        egressPacketHandler.notify(
//            player.pid,
//            EgressDataPacket.EquipmentUpdate(
//
//            )
//        )
    }

    fun notifyPlayerStats(player: Player) {
        queue.put(
            player.pid,
            CreatureStatsUpdate(
                defense = CreatureStatsUpdate.CreatureIntStatPacket(
                    player.stats.defense.base,
                    player.stats.defense.current
                ),
                attack = CreatureStatsUpdate.CreatureIntStatPacket(
                    player.stats.attack.base,
                    player.stats.attack.current
                ),
                attackSpeed = CreatureStatsUpdate.CreatureFloatStatPacket(
                    player.stats.attackSpeed.base,
                    player.stats.attackSpeed.current
                ),
                movementSpeed = CreatureStatsUpdate.CreatureFloatStatPacket(
                    player.stats.movementSpeed.base,
                    player.stats.movementSpeed.current
                ),
                healthPool = CreatureStatsUpdate.CreatureIntStatPacket(
                    player.stats.healthPool.base,
                    player.stats.healthPool.current
                ),
            )
        )
    }

    fun notifyBackpackUpdate(player: Player) {
        queue.put(
            player.pid,
            BackpackUpdate(
                items = player.backpack.getAll().map {
                    if (it == null) {
                        return@map null
                    }
                    return@map BackpackUpdate.BackpackSlot(
                        itemSchemaId = it.itemSchema.id.value,
                        stackCount = it.stackCount
                    )
                }.toTypedArray()
            )
        )
    }

    fun notifyPingResponse(pid: PID) {
        queue.put(
            pid,
            PingResponse()
        )
    }

    fun notifyWallsUpdate(pid: PID, gameMap: GameMap) {
        val chains = gameMap.walls.map { wall ->
            if (wall.fixtureList.size > 1) {
                throw Error("More than 1 fixture not supported for a wall")
            }
            return@map getVerticesForShape(wall.fixtureList[0].shape)
        }
        queue.put(
            pid,
            TerrainWallsUpdate(
                chains = chains.toTypedArray()
            )
        )
    }

    private fun getVerticesForShape(shape: Shape): Array<ApiVector2> {
        return when (shape) {
            is ChainShape -> {
                val list = Array(shape.vertexCount) { WorldPosition() }

                for (i in 0..shape.vertexCount - 1) {
                    shape.getVertex(i, list[i])
                }

                list.map { it.convert() }.toTypedArray()
            }
            else -> throw Error("Shape ${shape.type} not supported as a wall")
        }
    }
}
