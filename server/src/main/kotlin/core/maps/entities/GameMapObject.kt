package core.maps.entities

import core.types.ItemInstanceId
import core.types.WorldPosition
import core.types.WorldRadius
import gameland.ItemActionHandler
import utils.getDistance

data class GameMapObject(
    val itemInstanceId: ItemInstanceId,
    var position: WorldPosition,
    val actionHandler: ItemActionHandler = ItemActionHandler.defaultImpl
) {
    val collisionRadius = WorldRadius(64)

    fun collidesWith(checkedPosition: WorldPosition): Boolean {
        return getDistance(position, checkedPosition) < collisionRadius.value
    }
}
