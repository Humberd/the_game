package clientjvm.exts

import io.reactivex.rxjava3.subjects.ReplaySubject

class Promise<Input, Output>(
    val data: Input,
) {
    val emitter = ReplaySubject.create<Output>(1)

    fun resolve(output: Output) {
        emitter.onNext(output)
    }
}
