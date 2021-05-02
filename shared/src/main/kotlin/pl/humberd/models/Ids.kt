package pl.humberd.models


/**
 * Player ID
 *
 * Uniquely identifies a player in a match
 */
@JvmInline
value class PID(val value: UInt) {
    companion object {
        private var counter = 0u;
        fun unique() = PID(++counter)
    }
}

/**
 * Creature ID
 *
 * Uniquely identifies a creature in a match
 */
@JvmInline
value class CID(val value: UInt) {
    companion object {
        private var counter = 0u
        fun unique() = CID(++counter)
        fun empty() = CID(0u)
    }

    fun notEmpty(): CID {
        if (value == 0u) {
            throw Error("CID is empty")
        }

        return this
    }
}

/**
 * Spell ID
 *
 * Uniquely identifies a spell
 */
@JvmInline
value class SID(val value: UInt) {
    companion object {
        private var counter = 0u
        fun unique() = SID(++counter)
    }
}

@JvmInline
value class Experience(val value: Long) {
    init {
        require(value >= 0)
    }

    fun toLevel() = Level((value / 100f).toInt() + 1)
    operator fun minus(exp: Experience) = Experience(value - exp.value)
}

@JvmInline
value class Level(val value: Int) {
    init {
        require(value > 0)
    }

    fun next() = Level(value + 1)
    fun expRequired() = Experience((value - 1) * 100L)
}
