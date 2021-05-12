package core.maps.entities.creatures

import core.maps.entities.Tile
import core.types.GridPosition
import core.types.WorldPosition
import utils.toGridPosition

class CreatureLastUpdate(
    private val creature: Creature,
) {
    lateinit var gridPosition: GridPosition
    lateinit var tileSlice: Array<Array<Tile>>

    fun onInit(position: WorldPosition) {
        gridPosition = toGridPosition(position)
        tileSlice = creature.gameMap.getTilesAround(gridPosition, creature.tilesViewRadius.value)
    }
}
