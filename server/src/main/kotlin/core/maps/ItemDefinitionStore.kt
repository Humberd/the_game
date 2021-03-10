package core.maps

import com.beust.klaxon.Klaxon
import core.types.WorldRadius
import errors.UnknownItemId

enum class ItemType(val id: Int) {
    JUST_A_KNIFE(1),
    WOODEN_SHIELD(2),
    GOLD_BAR(3),
    TELEPORT(4);

    companion object {
        fun from(id: Int): ItemType {
            return values().find { it.id == id }!!
        }
    }
}

data class ItemDef(
    val type: ItemType,
    val isMovable: Boolean,
    val collisionRadius: WorldRadius = WorldRadius(64)
)

private data class JsonItemDef(
    val id: Int,
    val name: String,
    val spriteId: Int,
    val isMovable: Boolean
)

object ItemDefinitionStore {
    private val map = HashMap<ItemType, ItemDef>()

    fun readItemsFromJson() {
        val json = javaClass.getResource("/resources/items.json").readText()
        Klaxon().parseArray<JsonItemDef>(json)?.forEach {
            val type = ItemType.from(it.id)
            map[type] = ItemDef(
                type = type,
                isMovable = it.isMovable
            )
        }
    }

    fun get(type: ItemType): ItemDef {
        return map[type]?.copy() ?: throw UnknownItemId(type)
    }

}
