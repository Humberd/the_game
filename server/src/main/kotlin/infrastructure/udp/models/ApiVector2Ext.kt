package infrastructure.udp.models

import core.types.WorldPosition
import pl.humberd.udp.models.ApiVector2

fun ApiVector2.convert(): WorldPosition {
    return WorldPosition(this.x, this.y)
}
