package clientjvm.global

import clientjvm.exts.emitter

object GameStats {
    val pingStream by emitter<Long>()
    val fpsStream by emitter<Long>()
}
