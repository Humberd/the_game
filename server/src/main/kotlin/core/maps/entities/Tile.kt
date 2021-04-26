package core.maps.entities

import core.maps.entities.creatures.Creature

import core.types.GridPosition
import core.types.ItemInstanceId
import core.types.SpriteId
import pl.humberd.models.CID

data class Tile(
    val spriteId: SpriteId,
    val gridPosition: GridPosition,
) {
    val items: TileContainer<ItemInstanceId, GameMapObject> = TileContainer()
    val creatures: TileContainer<CID, Creature> = TileContainer()

    class TileContainer<Id, Item> {
        private val map = hashMapOf<Id, Item>()

        fun put(id: Id, item: Item) {
            if (map.containsKey(id)) {
                throw Error("Cannot put ${item}, because it is already there.")
            }

            map[id] = item
        }

        fun remove(id: Id) {
            if (!map.containsKey(id)) {
                throw Error("Cannot remove ${id}, because it is not there.")
            }

            map.remove(id)
        }

        fun writeTo(buffer: ArrayList<Item>) {
            map.values.forEach { buffer.add(it) }
        }

        fun transferTo(id: Id, item: Item, container: TileContainer<Id, Item>) {
            remove(id)
            container.put(id, item)
        }
    }

}
