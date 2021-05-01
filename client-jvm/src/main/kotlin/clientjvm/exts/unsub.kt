package clientjvm.exts

import io.reactivex.rxjava3.subjects.PublishSubject

fun emitter() = lazy {
    PublishSubject.create<Boolean>()
}
