package clientjvm.exts

import io.reactivex.rxjava3.subjects.PublishSubject

fun unsub() = lazy {
    PublishSubject.create<Boolean>()
}
