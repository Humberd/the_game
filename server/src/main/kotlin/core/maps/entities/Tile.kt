package core.maps.entities

import core.types.CID
import core.types.IID
import core.types.SpriteId

data class Tile(
    val spriteId: SpriteId,
    val gridPosition: GameMap.GridPosition,
    private val items: HashMap<IID, Item> = hashMapOf(),
    private val creatures: HashMap<CID, Creature> = hashMapOf()
) {
    fun putCreature(creature: Creature) {
        if (creatures.containsKey(creature.cid)) {
            throw Error("Cannot put creature to tile for ${creature}, because it is already there.")
        }

        creatures[creature.cid] = creature
    }

    fun removeCreature(cid: CID) {
        if (!creatures.containsKey(cid)) {
            throw Error("Cannot remove creature from tile for ${cid}, because it is not there.")
        }

        creatures.remove(cid)
    }

    fun writeCreaturesTo(buffer: ArrayList<Creature>) {
        creatures.values.forEach {
            buffer.add(it)
        }
    }

    fun moveCreatureToTile(creature: Creature, targetTile: Tile) {
        removeCreature(creature.cid)
        targetTile.putCreature(creature)
    }

    fun putItem(item: Item) {
        if (items.containsKey(item.iid)) {
            throw Error("Cannot put item to tile for ${item}, because it is already there.")
        }

        items[item.iid] = item
    }

    fun removeItem(iid: IID) {
        if (!items.containsKey(iid)) {
            throw Error("Cannot remove from tile for ${iid}, because it's not there")
        }

        items.remove(iid)
    }

    fun writeItemsTo(buffer: ArrayList<Item>) {
        items.values.forEach {
            buffer.add(it)
        }
    }

    fun moveItemToTile(item: Item, targetTile: Tile) {
        removeItem(item.iid)
        targetTile.putItem(item)
    }
}
