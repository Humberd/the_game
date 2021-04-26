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
