package core.maps.entities.creatures

class CreatureCache(
    private val thisCreature: Creature
) {
    val creaturesThatSeeMe = HashSet<Creature>()
    val creaturesISee = VisibleCreatures()

    fun onInit() {
        // nothing
    }

    inner class VisibleCreatures {
        private val set = HashSet<Creature>()

        fun register(otherCreature: Creature) {
            if (set.contains(otherCreature)) {
                throw Error("Creature already exists")
            }

            set.add(otherCreature)
            otherCreature.cache.creaturesThatSeeMe.add(thisCreature)

            thisCreature.hooks.onOtherCreatureAppearInViewRange(otherCreature)
        }

        fun unregister(otherCreature: Creature) {
            if (!set.contains(otherCreature)) {
                throw Error("Creature doesn't exist")
            }

            set.remove(otherCreature)
            otherCreature.cache.creaturesThatSeeMe.remove(thisCreature)

            thisCreature.hooks.onOtherCreatureDisappearFromViewRange(otherCreature)
        }

        fun getAll(): Collection<Creature> {
            return ArrayList(set)
        }
    }
}
