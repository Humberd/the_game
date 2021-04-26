package infrastructure.udp.models

import com.badlogic.gdx.math.Vector2
import core.types.WorldPosition
import pl.humberd.models.ApiVector2

fun ApiVector2.convert(): WorldPosition {
    return WorldPosition(x, y)
}

fun Vector2.convert(): ApiVector2 {
    return ApiVector2(x, y)
}
