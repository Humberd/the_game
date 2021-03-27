package core.maps.entities.creatures

import core.maps.entities.items.Item

class CreatureBackpack(
    private val creature: Creature
) {
    private val size = 20
    private val list = Array<Item?>(size) { null }

    fun onInit(items: Array<Item?>) {
        require(items.size <= list.size)
        items.forEachIndexed { index, item -> list[index] = item }
    }

    fun putIntoFirstFreeSlot(item: Item) {
        check(!list.contains(item))

        val firstFreeSlot = list.indexOfFirst { it == null }
        check(firstFreeSlot != -1)

        list[firstFreeSlot] = item
    }

    fun getAll() = list.clone()
}
