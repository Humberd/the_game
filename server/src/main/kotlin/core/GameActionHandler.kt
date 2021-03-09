package core

import core.maps.entities.Player
import core.maps.entities.Spell
import core.maps.entities.SpellsContainer
import core.types.CID
import core.types.GameMapId
import core.types.SID
import core.types.SpriteId
import infrastructure.database.Database
import infrastructure.udp.ingress.IngressPacket
import mu.KotlinLogging
import utils.ms
import utils.sec

private val logger = KotlinLogging.logger {}

class GameActionHandler(
    private val gamesManager: GamesManager,
    private val database: Database
) {
    fun handle(action: IngressPacket.Disconnect) {
        if (action.pid == null) {
            return
        }

        gamesManager.removePlayer(action.pid)
    }

    fun handle(action: IngressPacket.AuthLogin) {
        val dbPlayer = database.getPlayer(action.pid)
        val playerCharacter = Player(
            pid = dbPlayer.id,
            cid = CID.unique(),
            name = dbPlayer.name,
            health = dbPlayer.health,
            spriteId = dbPlayer.spriteId,
            spellsContainer = SpellsContainer(
                spell1 = Spell(
                    sid = SID.unique(),
                    name = "Grizzly Beam",
                    spriteId = SpriteId(11u),
                    cooldown = 1500.ms
                ),
                spell3 = Spell(
                    sid = SID.unique(),
                    name = "Mega inferno blast",
                    spriteId = SpriteId(9u),
                    cooldown = 12.sec
                )
            )
        )

        gamesManager.addPlayer(playerCharacter, GameMapId(1u))
    }

    fun handle(action: IngressPacket.PositionChange) {
        val direction = action.direction.toDirection().toVector2()
        gamesManager.movePlayerBy(action.pid, direction)
    }

    fun handle(action: IngressPacket.TerrainItemDrag) {
        gamesManager.dragItemOnTerrain(action.pid, action.iid, action.targetPosition)
    }

    fun handle(action: IngressPacket.SpellUsage) {
        gamesManager.useSpell(action.pid,action.sid)
    }
}
