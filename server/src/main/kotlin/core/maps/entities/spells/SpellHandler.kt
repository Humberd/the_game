package core.maps.entities.spells

import core.types.WorldPosition

abstract class SpellHandler {
    open fun onCastStart(targetPosition: WorldPosition) {}
    open fun onCastEnd(targetPosition: WorldPosition) {}
}
