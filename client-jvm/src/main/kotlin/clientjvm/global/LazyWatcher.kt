package clientjvm.global

import godot.core.memory.GodotStatic
import io.reactivex.rxjava3.subjects.PublishSubject

object LazyWatcher : GodotStatic {
    val onDestroyed = PublishSubject.create<Any>()

    init {
        registerAsSingleton()
    }

    override fun collect() {
        onDestroyed.onNext("end")
    }
}
