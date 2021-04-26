package pl.humberd.models

@JvmInline
value class Milliseconds(val value: UInt)

val Int.ms: Milliseconds
    get() {
        require(this >= 0)
        return Milliseconds(this.toUInt())
    }

val Float.sec: Milliseconds
    get() = (this * 1000).toInt().ms

val Int.sec: Milliseconds
    get() = (this * 1000).ms

val Int.min: Milliseconds
    get() = (this * 60).sec

