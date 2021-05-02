package clientjvm.exts

import io.reactivex.rxjava3.subjects.PublishSubject

fun emitter() = emitter<Boolean>()

@JvmName("typedEmitter")
fun <T> emitter() = lazy {
    PublishSubject.create<T>()
}

