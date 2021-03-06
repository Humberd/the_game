package core.maps.entities

import core.types.CID
import core.types.CreatureName
import core.types.SpriteId
import core.types.WorldPosition
import org.mini2Dx.gdx.math.Vector2

class Monster(
    cid: CID,
    name: CreatureName,
    health: UInt,
    spriteId: SpriteId,
    position: WorldPosition = Vector2(0f, 0f)
) : Creature(cid, name, health, spriteId, position)
