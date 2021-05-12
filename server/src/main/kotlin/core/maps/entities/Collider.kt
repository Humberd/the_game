package core.maps.entities

import core.maps.entities.creatures.Creature

interface Collider<T> {
    interface WithCreature : Collider<Creature>

    fun onCollisionStart(entity: T)
    fun onCollisionEnd(entity: T)
}
