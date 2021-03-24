package core.maps.entities.items

enum class ModificationType {
    FLAT {
        override fun calculateNewCurrent(base: Float, current: Float, saidValue: Int): Float {
            return saidValue + current
        }

        override fun calculateNewCurrent(base: Int, current: Int, saidValue: Int): Int {
            return saidValue + current
        }
    },

    /**
     * We are assuming [saidValue] is:
     * 10f = 10%
     * 100f = 100%
     */
    BASE_PERCENTAGE {
        override fun calculateNewCurrent(base: Float, current: Float, saidValue: Int): Float {
            return current + base * saidValue / 100
        }

        override fun calculateNewCurrent(base: Int, current: Int, saidValue: Int): Int {
            return current + (base * saidValue / 100f).toInt()
        }
    };

    abstract fun calculateNewCurrent(base: Float, current: Float, saidValue: Int): Float
    abstract fun calculateNewCurrent(base: Int, current: Int, saidValue: Int): Int
}
