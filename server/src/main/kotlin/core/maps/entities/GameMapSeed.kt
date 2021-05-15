package core.maps.entities

import core.maps.entities.creatures.monster.MonsterSeed
import org.recast4j.recast.geom.InputGeomProvider

data class GameMapSeed(
    val grid: Array<Array<Tile>>,
    val geometryProvider: InputGeomProvider,
    val monsterSeeds: List<MonsterSeed>
)
