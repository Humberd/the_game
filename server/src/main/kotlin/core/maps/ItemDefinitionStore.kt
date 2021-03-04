package core.maps

import com.beust.klaxon.Klaxon
import core.types.ItemId
import core.types.SpriteId
import errors.UnknownItemId

data class ItemDef(
    val id: ItemId,
    val name: String,
    val spriteId: SpriteId
)

private data class JsonItemDef(
    val id: Int,
    val name: String,
    val spriteId: Int,
)

object ItemDefinitionStore {
    private val map = HashMap<ItemId, ItemDef>()

    fun readItemsFromJson() {
        val json = this.javaClass.getResource("/resources/items/items.json").readText()
        Klaxon().parseArray<JsonItemDef>(json)?.forEach {
            val id = ItemId(it.id.toUShort())

            map[id] = ItemDef(
                id = id,
                name = it.name,
                spriteId = SpriteId(it.spriteId.toUShort())
            )
        }
    }

    fun get(itemId: ItemId): ItemDef {
        return map[itemId] ?: throw UnknownItemId(itemId)
    }

}
