package core.maps.entities.items

abstract class Item(
    val itemSchema: ItemSchema,
    stackCount: UShort = 1u
) {
    companion object {
        const val MAX_STACK_COUNT = 100u
    }

    var stackCount: UShort = stackCount
        private set(value) {
            require(value > 0u && value <= MAX_STACK_COUNT)
            if (!itemSchema.stackable) require(value == (1).toUShort())
            field = value
        }
}
