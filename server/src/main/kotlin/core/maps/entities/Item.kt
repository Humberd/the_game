package core.maps.entities

import core.maps.ItemDef
import core.types.IID
import core.types.WorldPosition
import core.types.WorldRadius
import gameland.ItemActionHandler

data class Item(
    val iid: IID,
    val itemDef: ItemDef,
    var position: WorldPosition,
    val actionHandler: ItemActionHandler = ItemActionHandler.defaultImpl
) {
    val collisionRadius = WorldRadius(64)

    fun collidesWith(checkedPosition: WorldPosition): Boolean {
        val ac = Math.abs(position.x - checkedPosition.x)
        val cb = Math.abs(position.y - checkedPosition.y)

        val distance = Math.hypot(ac.toDouble(), cb.toDouble())

        return distance < collisionRadius.value
    }
}
