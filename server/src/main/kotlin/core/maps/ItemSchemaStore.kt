package core.maps

import com.beust.klaxon.Klaxon
import core.maps.entities.creatures.EquipmentSlotType
import core.maps.entities.items.ItemSchema
import core.types.ItemSchemaId
import errors.UnknownItemId
import infrastructure.database.types.Equippable
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private data class JsonItemSchema(
    val id: Int,
    val equippable: List<String>,
    val stackable: Boolean
) {
    fun toRegularSchema(): ItemSchema {
        return ItemSchema(
            id = ItemSchemaId(id.toUShort()),
            equippable = Equippable.within(*equippable.map { EquipmentSlotType.valueOf(it) }.toTypedArray()),
            stackable = stackable
        )
    }
}

object ItemSchemaStore {
    private val map = HashMap<ItemSchemaId, ItemSchema>()

    fun readItemsFromJson() {
        logger.debug { "Reading items_schemas.json" }
        val json = javaClass.getResource("/assets/item_schemas.json").readText()
        Klaxon().parseArray<JsonItemSchema>(json)?.forEach {
            val itemSchema = it.toRegularSchema()
            map[itemSchema.id] = itemSchema
        }
        logger.debug { "Reading items_schemas.json - DONE" }
    }

    fun get(id: ItemSchemaId): ItemSchema {
        return map[id] ?: throw UnknownItemId(id)
    }

}
