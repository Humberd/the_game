package core.maps.entities

import com.badlogic.gdx.physics.box2d.World
import core.maps.entities.creatures.Creature
import core.maps.obstacles.Obstacle
import core.types.GridPosition
import core.types.SpriteId
import pl.humberd.models.CID

data class Tile(
    val spriteId: SpriteId,
    val gridPosition: GridPosition,
    val obstacles: List<Obstacle> = emptyList()
) {
    val creatures: TileContainer<CID, Creature> = TileContainer()

    fun onInit(physics: World) {
        obstacles.forEach { it.onInit(physics) }
    }

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
