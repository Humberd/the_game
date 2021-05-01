package clientjvm.exts

import clientjvm.global.LazyWatcher

fun <T> godotLazy(initializer: () -> T): Lazy<T> = GodotSynchronizedLazyImpl(initializer)

internal object UNINITIALIZED_VALUE

private class GodotSynchronizedLazyImpl<out T>(initializer: () -> T, lock: Any? = null) : Lazy<T> {
    private var initializer: (() -> T)? = initializer

    @Volatile
    private var _value: Any? = UNINITIALIZED_VALUE

    // final field is required to enable safe publication of constructed instance
    private val lock = lock ?: this

    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    @Suppress("UNCHECKED_CAST") (_v2 as T)
                } else {
                    val typedValue = initializer!!()
                    _value = typedValue
                    initializer = null
                    typedValue
                }
            }
        }

    init {
        LazyWatcher.onDestroyed
            .take(1)
            .subscribe {
                _value = UNINITIALIZED_VALUE
            }
    }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."
}
