package clientjvm.global

import clientjvm.exts.emitter

object GameStats {
    val pingStream by emitter<Long>()
    val fpsStream by emitter<Long>()
    val bytesSent by emitter<Long>()
    val bytesReceived by emitter<Long>()
}
