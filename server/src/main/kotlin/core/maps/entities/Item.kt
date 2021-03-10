package core.maps.entities

import core.maps.ItemDef
import core.types.IID
import core.types.WorldPosition
import core.types.WorldRadius
import gameland.ItemActionHandler
import utils.getDistance

data class Item(
    val iid: IID,
    val itemDef: ItemDef,
    var position: WorldPosition,
    val actionHandler: ItemActionHandler = ItemActionHandler.defaultImpl
) {
    val collisionRadius = WorldRadius(64)

    fun collidesWith(checkedPosition: WorldPosition): Boolean {
        return getDistance(position, checkedPosition) < collisionRadius.value
    }
}
